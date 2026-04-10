package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.entity.bo.Loan;
import fr.eni.bookhubbackend.entity.bo.dto.LoanRequestDto;
import fr.eni.bookhubbackend.entity.bo.dto.LoanResponseDto;
import fr.eni.bookhubbackend.mapper.LoanMapper;
import fr.eni.bookhubbackend.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Emprunts", description = "Gestion des emprunts et retours de livres")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    @PostMapping
    @Operation(summary = "Créer un emprunt", description = "Associe un livre à un utilisateur s'il est dispo et quota non atteint.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Emprunt créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur métier (Quota, dispo) ou ID introuvable")
    })
    public ResponseEntity<?> createLoan(@Valid @RequestBody LoanRequestDto req, Principal principal) {
        Loan newLoan = loanService.createLoan(principal.getName(), req.getBookId());
        LoanResponseDto response = loanMapper.toDto(newLoan);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Rendre un livre", description = "Marque l'emprunt comme terminé et rend le livre à nouveau disponible.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livre rendu avec succès"),
            @ApiResponse(responseCode = "400", description = "Livre déjà rendu"),
            @ApiResponse(responseCode = "404", description = "Emprunt introuvable")
    })
    public ResponseEntity<LoanResponseDto> returnLoan(@PathVariable("id") Long id, Principal principal) {
        Loan returnedLoan = loanService.returnLoan(id, principal.getName());
        LoanResponseDto response = loanMapper.toDto(returnedLoan);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @Operation(summary = "Mes emprunts", description = "Récupère la liste des emprunts de l'utilisateur connecté.")
    public ResponseEntity<List<LoanResponseDto>> getMyLoans(Principal principal) {
        String email = principal.getName();
        List<Loan> myLoans = loanService.getMyLoans(email);
        List<LoanResponseDto> response = myLoans.stream()
                .map(loanMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    @Operation(summary = "Tous les emprunts (Back-office)", description = "Réservé aux bibliothécaires. Récupère l'historique complet.")
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        List<Loan> allLoans = loanService.getAllLoans();
        List<LoanResponseDto> response = allLoans.stream()
                .map(loanMapper::toDto)
                .toList();

        return ResponseEntity.ok(response);
    }
}