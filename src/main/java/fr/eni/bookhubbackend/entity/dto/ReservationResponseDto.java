package fr.eni.bookhubbackend.entity.dto;

import java.time.LocalDateTime;

public record ReservationResponseDto(long id, long bookId, long userId, String bookTitle, String bookImage, LocalDateTime date, long queueRank, boolean canBorrow) {
}
