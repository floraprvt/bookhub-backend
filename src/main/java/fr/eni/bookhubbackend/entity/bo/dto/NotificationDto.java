package fr.eni.bookhubbackend.entity.bo.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        String message,
        Boolean isRead,
        LocalDateTime createdAt
) {}