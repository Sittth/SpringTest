package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.create.CourseCreateRequest;
import spring.ru.springtest.dto.response.CourseResponse;
import spring.ru.springtest.dto.response.StudentResponse;
import spring.ru.springtest.dto.update.CourseUpdateRequest;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.models.StudentModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {

    CourseResponse toResponse(CourseModel course);

    @Mapping(target = "students", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    CourseModel toEntity(CourseCreateRequest request);

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
}