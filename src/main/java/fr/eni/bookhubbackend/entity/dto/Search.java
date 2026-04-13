package fr.eni.bookhubbackend.entity.dto;

import java.time.Year;
import java.util.List;

public record Search(String title, List<Long> categoryList, List<Long> authors, Year date, Boolean isAvailable, String isbn) {
}
