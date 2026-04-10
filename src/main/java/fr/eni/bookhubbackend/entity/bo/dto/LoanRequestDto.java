package fr.eni.bookhubbackend.entity.bo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRequestDto {
    @NotNull
    private Long bookId;
}