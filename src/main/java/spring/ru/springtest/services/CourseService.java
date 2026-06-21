package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.create.CourseCreateRequest;
import spring.ru.springtest.dto.response.CourseResponse;
import spring.ru.springtest.dto.update.CourseUpdateRequest;
import spring.ru.springtest.dto.update.StudentUpdateRequest;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.CourseMapper;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.models.StudentModel;
import spring.ru.springtest.repositories.CourseRepository;
import spring.ru.springtest.repositories.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final StudentRepository studentRepository;

    private CourseModel findExistingCourse(UUID id) {
        return courseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Course not found with id {}", id);
                    return new EntityNotFoundException("Course " + id);
                });
    }

    private CourseModel findExistingCourseForUpdate(UUID id) {
        return courseRepository.findByIdAndIsDeletedFalseForUpdate(id)
                .orElseThrow(() -> {
                    log.error("Course not found with id {}", id);
                    return new EntityNotFoundException("Course " + id);
                });
    }

    @Transactional(readOnly = true)
    public CourseResponse findById(UUID id) {

        log.info("Search course by id {}", id);

        CourseModel courseModel = findExistingCourse(id);

        return courseMapper.toResponse(courseModel);
    }

    @Transactional(readOnly = true)
    public Page<CourseResponse> findAll(int page, int size) {

        log.info("Fetching courses page {} with size {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        Page<CourseModel> coursePage = courseRepository.findAllByIsDeletedFalse(pageable);

        return coursePage.map(courseMapper::toResponse);
    }

    @Transactional
    public CourseResponse save(CourseCreateRequest requestCreate) {

        log.info("Save course {}", requestCreate);

        CourseModel course = courseMapper.toEntity(requestCreate);

        if (requestCreate.getStudentIds() != null && !requestCreate.getStudentIds().isEmpty()) {
            List<StudentModel> students = studentRepository.findAllById(requestCreate.getStudentIds());
            if (students.size() != requestCreate.getStudentIds().size()) {
                throw new EntityNotFoundException("Some students not found");
            }
            students.forEach(course::addStudent);
        }

        CourseModel saved = courseRepository.save(course);

        log.info("Saved course with id {}", saved.getId());

        return courseMapper.toResponse(saved);
    }

    @Transactional
    public CourseResponse update(UUID id, CourseUpdateRequest requestUpdate) {

        log.info("Update course with id: {}", id);

        CourseModel existingCourse = findExistingCourseForUpdate(id);

        courseMapper.updateEntityFromDto(requestUpdate, existingCourse);

        new ArrayList<>(existingCourse.getStudents())
                .forEach(existingCourse::removeStudent);

        List<StudentModel> students =
                studentRepository.findAllById(requestUpdate.getStudentIds());

        if (students.size() != requestUpdate.getStudentIds().size()) {
            throw new EntityNotFoundException("Some students not found");
        }

        students.forEach(existingCourse::addStudent);

        log.info("Updated course with id {}", id);

        return courseMapper.toResponse(existingCourse);
    }

    @Transactional
    public void delete(UUID id) {

        log.info("Delete course with id {}", id);

        CourseModel courseModel = findExistingCourseForUpdate(id);

        courseRepository.delete(courseModel);

        log.info("Deleted course with id {}", id);
    }
}
