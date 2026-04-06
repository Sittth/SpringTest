package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.CourseRequestCreate;
import spring.ru.springtest.dto.CourseRequestUpdate;
import spring.ru.springtest.dto.CourseResponse;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.CourseMapper;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.repositories.CourseRepository;
import spring.ru.springtest.repositories.StudentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public CourseResponse findById(UUID id) {

        log.debug("Search course by id {}", id);

        CourseModel courseModel = courseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Course not found with id {}", id);
                    return new EntityNotFoundException("Course", id);
                });
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

        CourseModel existingCourse = courseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Update failed: course not found with id {}", id);
                    return new EntityNotFoundException("Course", id);
                });

        courseMapper.updateEntityFromDto(requestUpdate, existingCourse);

        CourseModel saved = courseRepository.save(existingCourse);

        log.info("Updated course with id {}", id);

        return courseMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {

        log.debug("Delete course with id {}", id);

        CourseModel courseModel = courseRepository.findByIdAndIsDeletedFalse(id).
                orElseThrow(() -> {
                    log.warn("Attempt to delete non-existent or already deleted course with id: {}", id);
                    return new EntityNotFoundException("Course", id);
                });

        courseRepository.delete(courseModel);

        log.info("Deleted course with id {}", id);
    }
}
