package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.create.NotificationCreateRequest;
import spring.ru.springtest.dto.response.NotificationResponse;
import spring.ru.springtest.models.NotificationModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {

    @Mapping(target = "status", source = "notificationStatus")
    NotificationResponse toResponse(NotificationModel notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    NotificationModel toEntity(NotificationCreateRequest dto);
}
