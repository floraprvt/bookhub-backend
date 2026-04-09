package fr.eni.bookhubbackend.entity.bo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{12,}$",
            message = "12 car. min, 1 maj, 1 min, 1 chiffre, 1 spécial"
    )
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
