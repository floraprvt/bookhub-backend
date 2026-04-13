package fr.eni.bookhubbackend.controller;


import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.dto.CreateReservationDto;
import fr.eni.bookhubbackend.service.ReservationService;
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
    public ResponseEntity<Void> createReservation(@RequestBody CreateReservationDto createReservationDto, @AuthenticationPrincipal User user) {
        reservationService.createReservation(user, createReservationDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("my")
    public ResponseEntity<List<CreateReservationDto>> findMyReservations(@AuthenticationPrincipal final User user) {
        return ResponseEntity.ok(reservationService.findMyReservations(user.getId()));
    }

    @DeleteMapping("my")
    public ResponseEntity<Void> deleteMyReservations(@AuthenticationPrincipal final User user, final Long bookId) {
        reservationService.deleteMyReservation(user.getId(), bookId);
        return ResponseEntity.noContent().build();
    }
}
