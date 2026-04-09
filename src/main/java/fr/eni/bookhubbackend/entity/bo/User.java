package fr.eni.bookhubbackend.entity.bo;

import fr.eni.bookhubbackend.entity.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String lastName;

    @NotNull
    private String firstName;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String password;

    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
}
