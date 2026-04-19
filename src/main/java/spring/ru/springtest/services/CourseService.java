package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.CourseRequestCreate;
import spring.ru.springtest.dto.CourseRequestUpdate;
import spring.ru.springtest.dto.CourseResponse;
import spring.ru.springtest.dto.StudentRequestUpdate;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.CourseMapper;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.models.StudentModel;
import spring.ru.springtest.repositories.CourseRepository;
import spring.ru.springtest.repositories.StudentRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final StudentRepository studentRepository;

    private StudentModel findStudent(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student", id));
    }

    private CourseModel findExistingCourse(UUID id) {
        return courseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Course not found with id {}", id);
                    return new EntityNotFoundException("Course " + id);
                });
    }

    private StudentModel processStudent(StudentRequestUpdate dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("Student id is required");
        }

        return findStudent(dto.getId());
    }

    @Transactional(readOnly = true)
    public CourseResponse findById(UUID id) {

        log.debug("Search course by id {}", id);

        CourseModel courseModel = findExistingCourse(id);

        return courseMapper.toResponse(courseModel);
    }

    @Transactional
    public CourseResponse save(CourseRequestCreate requestCreate) {

        log.debug("Save course {}", requestCreate);

        CourseModel entity = courseMapper.toEntity(requestCreate);
        CourseModel saved = courseRepository.save(entity);

        log.info("Saved course with id {}", saved.getId());

        return courseMapper.toResponse(saved);
    }

    @Transactional
    public CourseResponse update(UUID id, CourseRequestUpdate requestUpdate) {

        log.debug("Update course with id: {}", id);

        CourseModel existingCourse = findExistingCourse(id);

        courseMapper.updateEntityFromDto(requestUpdate, existingCourse);

        if (requestUpdate.getStudents() != null) {

            for (StudentModel student : existingCourse.getStudents()) {
                student.getCourses().remove(existingCourse);
            }

            existingCourse.getStudents().clear();

            List<StudentModel> students = requestUpdate.getStudents().stream()
                    .map(this::processStudent)
                    .toList();

            existingCourse.getStudents().clear();

            for (StudentModel student : students) {
                existingCourse.getStudents().add(student);
                student.getCourses().add(existingCourse);
            }
        }

        log.info("Updated course with id {}", id);

        return courseMapper.toResponse(courseRepository.save(existingCourse));
    }

    @Transactional
    public void delete(UUID id) {

        log.debug("Delete course with id {}", id);

        CourseModel courseModel = findExistingCourse(id);

        courseRepository.delete(courseModel);

        log.info("Deleted course with id {}", id);
    }
}
