package fr.eni.bookhubbackend.entity.bo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequestDto {

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String phone;
}
