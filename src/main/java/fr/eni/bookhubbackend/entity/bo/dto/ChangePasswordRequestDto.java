package fr.eni.bookhubbackend.entity.bo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordRequestDto {

    @NotBlank
    private String currentPassword;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{12,}$",
            message = "Le mot de passe doit contenir 12 car. min, 1 maj, 1 min, 1 chiffre, 1 spécial"
    )
    private String newPassword;
}
