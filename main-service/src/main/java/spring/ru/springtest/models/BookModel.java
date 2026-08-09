package spring.ru.springtest.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "books", schema = "test")
@Getter
@Setter
@SQLDelete(sql = "UPDATE test.books SET is_deleted = true, updated_at = now() WHERE id=?")
public class BookModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private AuthorModel author;

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
