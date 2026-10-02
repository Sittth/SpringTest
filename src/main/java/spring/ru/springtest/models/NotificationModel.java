package spring.ru.springtest.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import spring.ru.springtest.models.enums.NotificationStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications", schema = "test")
@Getter
@Setter
public class NotificationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String recipient;

    @Column
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus notificationStatus;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = true)
    private OffsetDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @PrePersist
    public void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
