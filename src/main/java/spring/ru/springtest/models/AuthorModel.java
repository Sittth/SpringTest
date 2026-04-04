package spring.ru.springtest.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "authors", schema = "test")
@Getter
@Setter
@SQLDelete(sql = "UPDATE authors SET is_deleted = true, updated_at = now() WHERE id=?")
public class AuthorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BookModel> books;

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
