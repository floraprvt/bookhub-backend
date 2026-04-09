package fr.eni.bookhubbackend.entity.bo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    // TODO Regex
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
