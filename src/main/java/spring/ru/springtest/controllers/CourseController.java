package spring.ru.springtest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.CoursesApi;
import spring.ru.springtest.dto.Course;
import spring.ru.springtest.mapper.CourseMapper;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.services.CourseService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CourseController implements CoursesApi {

    private final CourseService courseService;
    private final CourseMapper courseMapper;

    @Override
    public ResponseEntity<Course> getCourseById(UUID id) {
        CourseModel model = courseService.findById(id);

        Course dto = courseMapper.toDto(model);

        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<Void> createCourseById(Course course) {
        CourseModel model = courseMapper.toEntity(course);

        courseService.save(model);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateCourseById(UUID id, Course course) {
        CourseModel model = courseMapper.toEntity(course);

        model.setId(id);
        courseService.update(id, model);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteCourseById(UUID id) {
        courseService.delete(id);

        return ResponseEntity.ok().build();
    }
}
