package spring.ru.springtest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.Course;
import spring.ru.springtest.mapper.CourseMapper;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.repositories.CourseRepository;

import java.util.UUID;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Autowired
    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Transactional(readOnly = true)
    public CourseModel findById(UUID id) {
        return courseRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public void save(CourseModel course) {
        courseRepository.save(course);
    }

    public void update(UUID id, Course dto) {

        CourseModel existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        courseMapper.updateEntityFromDto(dto, existingCourse);

        courseRepository.save(existingCourse);
    }

    public void delete(UUID id) {
        courseRepository.deleteById(id);
    }
}
