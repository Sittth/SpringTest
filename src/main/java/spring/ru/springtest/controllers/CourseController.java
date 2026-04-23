package spring.ru.springtest.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.CoursesApi;
import spring.ru.springtest.dto.*;
import spring.ru.springtest.services.CourseService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CourseController implements CoursesApi {

    private final CourseService courseService;

    @Override
    public ResponseEntity<CourseResponse> getCourseById(UUID id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @Override
    public ResponseEntity<CourseResponse> createCourse(@Valid CourseRequestCreate requestCreate) {
        CourseResponse created = courseService.save(requestCreate);

        return ResponseEntity.status(201).body(created);
    }

    @Override
    public ResponseEntity<StudentResponse> createStudentForCourse(
            @PathVariable("id") UUID id,
            @Valid @RequestBody StudentRequestCreate requestCreate) {
        StudentResponse created = courseService.createStudent(id, requestCreate);

        return ResponseEntity.status(201).body(created);
    }

    @Override
    public ResponseEntity<CourseResponse> updateCourseById(UUID id, @Valid CourseRequestUpdate requestUpdate) {
        CourseResponse updated = courseService.update(id, requestUpdate);

        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deleteCourseById(UUID id) {
        courseService.delete(id);

        return ResponseEntity.ok().build();
    }
}
