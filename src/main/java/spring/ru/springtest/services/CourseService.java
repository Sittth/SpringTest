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
import spring.ru.springtest.dto.create.StudentCreateRequest;
import spring.ru.springtest.dto.response.CourseResponse;
import spring.ru.springtest.dto.response.StudentResponse;
import spring.ru.springtest.dto.update.CourseUpdateRequest;
import spring.ru.springtest.dto.update.StudentUpdateRequest;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.CourseMapper;
import spring.ru.springtest.mapper.StudentMapper;
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
    private final StudentMapper studentMapper;

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

    private StudentModel processStudent(StudentUpdateRequest dto) {
        return findStudent(dto.getId());
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
    public StudentResponse createStudent(UUID id, StudentCreateRequest request) {

        log.info("Creating student for course id: {}", id);

        CourseModel course = findExistingCourse(id);

        StudentModel student = studentMapper.toEntity(request);

        course.addStudent(student);

        studentRepository.save(student);

        log.info("Created student with id: {} for course id: {}", student.getId(), id);

        return studentMapper.toResponse(student);
    }

    @Transactional
    public CourseResponse save(CourseCreateRequest requestCreate) {

        log.info("Save course {}", requestCreate);

        CourseModel entity = courseMapper.toEntity(requestCreate);
        CourseModel saved = courseRepository.save(entity);

        log.info("Saved course with id {}", saved.getId());

        return courseMapper.toResponse(saved);
    }

    @Transactional
    public CourseResponse update(UUID id, CourseUpdateRequest requestUpdate) {

        log.info("Update course with id: {}", id);

        CourseModel existingCourse = findExistingCourse(id);

        courseMapper.updateEntityFromDto(requestUpdate, existingCourse);

        if (requestUpdate.getStudents() != null) {

            List<StudentModel> students = requestUpdate.getStudents().stream()
                    .map(this::processStudent)
                    .toList();

            for (StudentModel student : students) {
                if (!existingCourse.getStudents().contains(student)) {
                    existingCourse.addStudent(student);
                }
            }
        }

        log.info("Updated course with id {}", id);

        return courseMapper.toResponse(courseRepository.save(existingCourse));
    }

    @Transactional
    public void delete(UUID id) {

        log.info("Delete course with id {}", id);

        CourseModel courseModel = findExistingCourse(id);

        courseRepository.delete(courseModel);

        log.info("Deleted course with id {}", id);
    }
}
