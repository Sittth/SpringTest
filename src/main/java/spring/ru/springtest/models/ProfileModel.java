package spring.ru.springtest.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "profiles", schema = "test")
@Getter
@Setter
@SQLDelete(sql = "UPDATE test.profiles SET is_deleted = true, updated_at = now() WHERE id=?")
public class ProfileModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String bio;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private UserModel user;

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