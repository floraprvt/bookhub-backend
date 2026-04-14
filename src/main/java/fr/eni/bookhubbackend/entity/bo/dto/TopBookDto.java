package fr.eni.bookhubbackend.entity.bo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopBookDto {

    private Long bookId;
    private String title;
    private long loanCount;
}