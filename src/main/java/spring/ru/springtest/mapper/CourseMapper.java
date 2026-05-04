package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.create.CourseCreateRequest;
import spring.ru.springtest.dto.response.CourseResponse;
import spring.ru.springtest.dto.update.CourseUpdateRequest;
import spring.ru.springtest.models.CourseModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = StudentMapper.class)
public interface CourseMapper {

    CourseResponse toResponse(CourseModel course);

    CourseModel toEntity(CourseCreateRequest requestCreate);

    void updateEntityFromDto(CourseUpdateRequest dto, @MappingTarget CourseModel course);
}