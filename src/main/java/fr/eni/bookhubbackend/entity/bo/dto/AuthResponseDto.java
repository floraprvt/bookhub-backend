package fr.eni.bookhubbackend.entity.bo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    private String token;
    private String role;
    private String email;
}
