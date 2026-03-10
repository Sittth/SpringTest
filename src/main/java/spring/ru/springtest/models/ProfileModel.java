package spring.ru.springtest.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "profiles", schema = "test")
@Getter
@Setter
public class ProfileModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String bio;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private UserModel user;
}