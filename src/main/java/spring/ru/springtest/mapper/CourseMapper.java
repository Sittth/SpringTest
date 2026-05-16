package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.create.CourseCreateRequest;
import spring.ru.springtest.dto.create.StudentCreateRequest;
import spring.ru.springtest.dto.response.CourseResponse;
import spring.ru.springtest.dto.response.StudentResponse;
import spring.ru.springtest.dto.update.CourseUpdateRequest;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.models.StudentModel;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {

    CourseResponse toResponse(CourseModel course);

    StudentResponse toStudentResponse(StudentModel student);

    List<StudentResponse> toStudentResponses(List<StudentModel> students);
}