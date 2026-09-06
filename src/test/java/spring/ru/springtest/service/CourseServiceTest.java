package spring.ru.springtest.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.response.CourseResponse;
import spring.ru.springtest.dto.update.CourseUpdateRequest;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.CourseMapper;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.repositories.CourseRepository;
import spring.ru.springtest.services.CourseService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseMapper courseMapper;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CourseService courseService;

    @Test
    void update_shouldFlushThenInsertStudentsThenRefresh_inCorrectOrder() {
        UUID courseId = UUID.randomUUID();
        UUID student1 = UUID.randomUUID();
        UUID student2 = UUID.randomUUID();

        CourseModel existing = new CourseModel();
        existing.setId(courseId);

        CourseUpdateRequest request = new CourseUpdateRequest()
                .title("Updated title")
                .studentIds(List.of(student1, student2));

        when(courseRepository.findByIdAndIsDeletedFalse(courseId)).thenReturn(Optional.of(existing));
        when(courseMapper.toResponse(existing)).thenReturn(new CourseResponse().id(courseId));

        courseService.update(courseId, request);

        InOrder inOrder = inOrder(entityManager, courseRepository);
        inOrder.verify(entityManager).flush();
        inOrder.verify(courseRepository).insertStudentToCourse(courseId, student1);
        inOrder.verify(courseRepository).insertStudentToCourse(courseId, student2);
        inOrder.verify(entityManager).refresh(existing);
    }

    @Test
    void update_shouldThrow_whenCourseNotFound() {
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findByIdAndIsDeletedFalse(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.update(courseId, new CourseUpdateRequest()))
                .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(entityManager);
    }

    @Test
    void findById_shouldReturnCourse_whenExists() {
        UUID id = UUID.randomUUID();
        CourseModel model = new CourseModel();
        model.setId(id);
        CourseResponse expected = new CourseResponse().id(id);

        when(courseRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(model));
        when(courseMapper.toResponse(model)).thenReturn(expected);

        assertThat(courseService.findById(id)).isEqualTo(expected);
    }

    @Test
    void delete_shouldRemoveCourse() {
        UUID id = UUID.randomUUID();
        CourseModel model = new CourseModel();
        model.setId(id);
        when(courseRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(model));

        courseService.delete(id);

        verify(courseRepository).delete(model);
    }
}
