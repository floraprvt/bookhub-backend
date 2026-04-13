package fr.eni.bookhubbackend.entity.dto;

import fr.eni.bookhubbackend.entity.bo.User;

public record CreateReservationDto(User user, Long bookId) {
}
