package fr.eni.bookhubbackend.entity.dto;

import java.io.Serializable;

public record CategoryDto(Long id, String name) implements Serializable {
}
