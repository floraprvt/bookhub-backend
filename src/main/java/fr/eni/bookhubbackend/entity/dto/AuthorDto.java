package fr.eni.bookhubbackend.entity.dto;

import java.io.Serializable;

public record AuthorDto(Long id, String firstName, String lastName) implements Serializable {
}
