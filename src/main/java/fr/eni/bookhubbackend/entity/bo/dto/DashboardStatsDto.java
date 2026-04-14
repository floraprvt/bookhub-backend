package fr.eni.bookhubbackend.entity.bo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    private long totalBooks;
    private long activeLoans;
    private long overdueLoans;
}