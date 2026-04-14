package fr.eni.bookhubbackend.entity.bo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {

    private Long id;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private Boolean isReturned;
    private Long userId;
    private String bookTitle;
    private boolean late;
    private Long bookId;
}