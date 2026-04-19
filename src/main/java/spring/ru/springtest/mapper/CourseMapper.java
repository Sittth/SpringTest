package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.CourseRequestCreate;
import spring.ru.springtest.dto.CourseRequestUpdate;
import spring.ru.springtest.dto.CourseResponse;
import spring.ru.springtest.models.CourseModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = StudentMapper.class)
public interface CourseMapper {

    CourseResponse toResponse(CourseModel course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    CourseModel toEntity(CourseRequestCreate requestCreate);

    @Mapping(target = "students", ignore = true)
    void updateEntityFromDto(CourseRequestUpdate dto, @MappingTarget CourseModel course);
}