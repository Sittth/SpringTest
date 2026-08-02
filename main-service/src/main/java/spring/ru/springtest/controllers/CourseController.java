package spring.ru.springtest.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.CoursesApi;
import spring.ru.springtest.dto.create.CourseCreateRequest;
import spring.ru.springtest.dto.response.CourseResponse;
import spring.ru.springtest.dto.update.CourseUpdateRequest;
import spring.ru.springtest.dto.response.GetCourses200Response;
import spring.ru.springtest.mapper.CourseMapper;
import spring.ru.springtest.services.CourseService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CourseController implements CoursesApi {

    private final CourseService courseService;
    private final CourseMapper courseMapper;

    @Override
    public CourseResponse getCourseById(UUID id) {

        return courseService.findById(id);
    }

    @Override
    public GetCourses200Response getCourses(Integer page, Integer size) {

        return courseMapper.toPageResponse(courseService.findAllPaginated(page, size));
    }

    @Override
    public CourseResponse createCourse(CourseCreateRequest requestCreate) {

        return courseService.save(requestCreate);
    }

    @Override
    public CourseResponse updateCourseById(UUID id, CourseUpdateRequest requestUpdate) {

        return courseService.update(id, requestUpdate);
    }

    @Override
    public void deleteCourseById(UUID id) {

        courseService.delete(id);
    }
}
