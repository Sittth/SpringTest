package spring.ru.springtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.dto.create.CourseCreateRequest;
import spring.ru.springtest.dto.response.CourseResponse;
import spring.ru.springtest.dto.update.CourseUpdateRequest;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.models.StudentModel;
import spring.ru.springtest.repositories.CourseRepository;
import spring.ru.springtest.repositories.StudentRepository;
import spring.ru.springtest.services.CourseService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourseServiceTest extends AbstractControllerTest {

    @Autowired
    CourseService courseService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Test
    void save_shouldPersistCourse() {
        CourseResponse response = courseService.save(new CourseCreateRequest("Java Architecture"));

        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Java Architecture");

        CourseModel persisted = courseRepository.findByIdAndIsDeletedFalse(response.getId()).orElseThrow();
        assertThat(persisted.getTitle()).isEqualTo("Java Architecture");
    }

    @Test
    void findById_shouldThrow_whenCourseNotFound() {
        assertThatThrownBy(() -> courseService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_shouldChangeTitle_andAttachStudents() {
        CourseResponse created = courseService.save(new CourseCreateRequest("Old Title"));

        StudentModel student1 = new StudentModel();
        student1.setName("Alice");
        student1 = studentRepository.save(student1);

        StudentModel student2 = new StudentModel();
        student2.setName("Bob");
        student2 = studentRepository.save(student2);

        CourseResponse updated = courseService.update(created.getId(), new CourseUpdateRequest()
                .title("New Title")
                .studentIds(List.of(student1.getId(), student2.getId())));

        assertThat(updated.getTitle()).isEqualTo("New Title");
        assertThat(updated.getStudents()).hasSize(2);
    }

    @Test
    void delete_shouldSoftDeleteCourse() {
        CourseResponse created = courseService.save(new CourseCreateRequest("To Delete"));

        courseService.delete(created.getId());

        assertThat(courseRepository.findByIdAndIsDeletedFalse(created.getId())).isEmpty();
    }
}
