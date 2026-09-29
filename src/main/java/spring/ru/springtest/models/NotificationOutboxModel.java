package spring.ru.springtest.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import spring.ru.springtest.models.enums.NotificationOutboxStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_outbox", schema = "test")
@Getter
@Setter
public class NotificationOutboxModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID notificationId;

    @Column
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationOutboxStatus notificationOutboxStatus;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private int attempts;

    @Column
    private OffsetDateTime lockedUntil;

    @Column
    private OffsetDateTime nextRetryAt;

    @PrePersist
    public void onCreate() {
        createdAt = OffsetDateTime.now();
        nextRetryAt = OffsetDateTime.now();
    }
}
