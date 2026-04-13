package fr.eni.bookhubbackend.entity.dto;

import java.time.LocalDateTime;

public record ReservationDto(long bookId, long userId, LocalDateTime date, int rank) {
}
