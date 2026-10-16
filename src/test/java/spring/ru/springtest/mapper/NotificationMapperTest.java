package spring.ru.springtest.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import spring.ru.springtest.dto.create.NotificationCreateRequest;
import spring.ru.springtest.dto.response.NotificationResponse;
import spring.ru.springtest.models.NotificationModel;
import spring.ru.springtest.models.enums.NotificationStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationMapperTest {

    private final NotificationMapper mapper = Mappers.getMapper(NotificationMapper.class);

    @Test
    void toEntity_shouldNotSetId_becauseItIsGeneratedBuDatabase() {
        NotificationModel entity = mapper.toEntity(new NotificationCreateRequest("bob", "hello"));

        assertThat(entity.getId()).isNull();
        assertThat(entity.getRecipient()).isEqualTo("bob");
        assertThat(entity.getMessage()).isEqualTo("hello");
    }

    @Test
    void toResponse_shouldCopyId_soClientCanFindNotificationLater() {
        UUID id = UUID.randomUUID();
        NotificationModel model = new NotificationModel();
        model.setId(id);
        model.setRecipient("bob");
        model.setMessage("hello");
        model.setNotificationStatus(NotificationStatus.PENDING);

        NotificationResponse response = mapper.toResponse(model);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getStatus()).isEqualTo("PENDING");
    }
}
