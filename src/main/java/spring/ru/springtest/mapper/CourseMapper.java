package spring.ru.springtest.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import spring.ru.springtest.dto.create.CourseCreateRequest;
import spring.ru.springtest.dto.response.CourseResponse;
import spring.ru.springtest.dto.response.GetCourses200Response;
import spring.ru.springtest.dto.response.StudentResponse;
import spring.ru.springtest.dto.update.CourseUpdateRequest;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.models.StudentModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {

    CourseResponse toResponse(CourseModel course);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.ERROR)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    CourseModel toEntity(CourseCreateRequest request);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.ERROR)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntityFromDto(CourseUpdateRequest dto, @MappingTarget CourseModel course);

    default StudentResponse toStudentResponse(StudentModel student) {
        if (student == null) return null;

        StudentResponse studentResponse = new StudentResponse();
        studentResponse.setId(student.getId());
        studentResponse.setName(student.getName());

        return studentResponse;
    }

    default GetCourses200Response toPageResponse(Page<CourseResponse> page) {
        return new GetCourses200Response()
            .content(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements());
    }
}