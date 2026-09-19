package spring.ru.springtest.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import spring.ru.springtest.models.enums.CacheInvalidationStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cache_invalidation_queue", schema = "test")
@Getter
@Setter
public class CacheInvalidationQueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String cacheName;

    @Column(nullable = false)
    private String cacheKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CacheInvalidationStatus status = CacheInvalidationStatus.PENDING;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private OffsetDateTime nextRetryAt;

    @Column
    private OffsetDateTime lockedUntil;

    @Column(columnDefinition = "text")
    private String lastError;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = OffsetDateTime.now();
        nextRetryAt = OffsetDateTime.now();
    }
}
