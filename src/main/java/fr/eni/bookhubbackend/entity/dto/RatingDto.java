package fr.eni.bookhubbackend.entity.dto;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.bo.User;

import java.io.Serializable;
import java.time.LocalDate;

public record RatingDto(Long id, LocalDate date, Double score, String comment, Book book, User user) implements Serializable {
}
