package spring.ru.springtest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import spring.ru.springtest.dto.create.StudentCreateRequest;
import spring.ru.springtest.dto.response.StudentResponse;
import spring.ru.springtest.dto.update.StudentUpdateRequest;
import spring.ru.springtest.models.StudentModel;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {

    StudentResponse toResponse(StudentModel student);

    List<StudentResponse> toResponses(List<StudentModel> students);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    StudentModel toEntity(StudentCreateRequest requestCreate);

    List<StudentModel> toEntity(List<StudentCreateRequest> requests);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    StudentModel toEntity(StudentUpdateRequest requestUpdate);
}