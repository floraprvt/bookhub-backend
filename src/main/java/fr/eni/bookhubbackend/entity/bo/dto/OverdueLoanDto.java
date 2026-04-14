package fr.eni.bookhubbackend.entity.bo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverdueLoanDto {

    private Long loanId;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String bookTitle;
    private LocalDate returnDate;
    private long daysOverdue;
}