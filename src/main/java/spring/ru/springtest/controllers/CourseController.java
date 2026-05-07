package spring.ru.springtest.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.CoursesApi;
import spring.ru.springtest.dto.create.CourseCreateRequest;
import spring.ru.springtest.dto.response.CourseResponse;
import spring.ru.springtest.dto.update.CourseUpdateRequest;
import spring.ru.springtest.dto.response.GetCourses200Response;
import spring.ru.springtest.services.CourseService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class CourseController implements CoursesApi {

    private final CourseService courseService;

    @Override
    public CourseResponse getCourseById(UUID id) {

        return courseService.findById(id);
    }

    @Override
    public GetCourses200Response getCourses(
            @Min(0) @NotNull Integer page,
            @Min(1) @Max(50) @NotNull Integer size) {

        Page<CourseResponse> result = courseService.findAll(page, size);

        GetCourses200Response response = new GetCourses200Response()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements());

        return response;
    }

    @Override
    public CourseResponse createCourse(@Valid CourseCreateRequest requestCreate) {

        return courseService.save(requestCreate);
    }

    @Override
    public CourseResponse updateCourseById(UUID id, @Valid CourseUpdateRequest requestUpdate) {

        return courseService.update(id, requestUpdate);
    }

    @Override
    public void deleteCourseById(UUID id) {

        courseService.delete(id);
    }
}
