package spring.ru.springtest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import spring.ru.springtest.dto.StudentRequestCreate;
import spring.ru.springtest.dto.StudentResponse;
import spring.ru.springtest.models.StudentModel;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {

    StudentResponse toResponse(StudentModel student);

    List<StudentResponse> toResponses(List<StudentModel> students);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    StudentModel toEntity(StudentRequestCreate requestCreate);

    List<StudentModel> toEntity(List<StudentRequestCreate> requests);
}