package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.Course;
import spring.ru.springtest.exceptions.CourseNotFoundException;
import spring.ru.springtest.mapper.CourseMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.repositories.CourseRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Transactional(readOnly = true)
    public Course findById(UUID id) {

        log.debug("Search course by id {}", id);

        CourseModel courseModel = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found with id {}", id);
                    return new CourseNotFoundException("Course with id " + id + " not found");
                });
        return courseMapper.toDto(courseModel);
    }

    @Transactional
    public void save(Course courseDto) {

        log.debug("Save course {}", courseDto);

        CourseModel courseModel = courseMapper.toEntity(courseDto);
        courseRepository.save(courseModel);

        log.info("Saved course with id {}", courseModel.getId());
    }

    @Transactional
    public void update(UUID id, Course dto) {

        log.debug("Update course with id: {}", id);

        CourseModel existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed: course not found with id {}", id);
                    return new CourseNotFoundException("Course with id " + id + " not found");
                });
        courseMapper.updateEntityFromDto(dto, existingCourse);
        courseRepository.save(existingCourse);

        log.info("Updated course with id {}", id);
    }

    @Transactional
    public void delete(UUID id) {

        log.debug("Delete course with id {}", id);

        if (!courseRepository.existsById(id)) {
            log.warn("Attempt to delete non-existent course with id: {}", id);
            throw new CourseNotFoundException("Course with id " + id + " not found");
        }
        courseRepository.deleteById(id);

        log.info("Deleted course with id {}", id);
    }
}
