package fr.eni.bookhubbackend.controller;


import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.dto.CreateReservationDto;
import fr.eni.bookhubbackend.entity.dto.ReservationResponseDto;
import fr.eni.bookhubbackend.service.ReservationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/reservations")
public class ReservationController {

    ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Void> createReservation(@Valid @RequestBody CreateReservationDto createReservationDto, @AuthenticationPrincipal User user) {
        reservationService.createReservation(user, createReservationDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("my")
    public ResponseEntity<List<ReservationResponseDto>> findMyReservations(@AuthenticationPrincipal final User user) {
        return ResponseEntity.ok(reservationService.findMyReservations(user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyReservations(@AuthenticationPrincipal final User user, @PathVariable final Long id) {
        reservationService.deleteReservation(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{bookId}")
    public boolean hasReserved(
            @AuthenticationPrincipal final User user,
            @PathVariable Long bookId
    ) {
        return reservationService.checkReservationExists(user.getId(), bookId);
    }


}
