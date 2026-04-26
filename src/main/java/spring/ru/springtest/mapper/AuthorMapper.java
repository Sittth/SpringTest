package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.dto.update.AuthorUpdateRequest;
import spring.ru.springtest.models.AuthorModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BookMapper.class)
public interface AuthorMapper {

    AuthorResponse toResponse(AuthorModel author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    AuthorModel toEntity(AuthorCreateRequest requestCreate);

    @Mapping(target = "books", ignore = true)
    void updateEntityFromDto(AuthorUpdateRequest dto, @MappingTarget AuthorModel author);
}