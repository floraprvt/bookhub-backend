package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.entity.bo.dto.ChangePasswordRequestDto;
import fr.eni.bookhubbackend.entity.bo.dto.ProfileResponseDto;
import fr.eni.bookhubbackend.entity.bo.dto.UpdateProfileRequestDto;
import fr.eni.bookhubbackend.entity.bo.dto.UpdateUserRoleRequestDto;
import fr.eni.bookhubbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Profil", description = "Gestion du profil utilisateur")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProfileResponseDto>> getAllUsers(
            @ParameterObject @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par son id (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Modifier le rôle d'un utilisateur (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileResponseDto> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequestDto dto) {
        return ResponseEntity.ok(userService.updateUserRole(id, dto));
    }

    @GetMapping("/me")
    @Operation(summary = "Consulter son profil")
    public ResponseEntity<ProfileResponseDto> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }

    @PutMapping("/me")
    @Operation(summary = "Modifier prénom,nom téléphone")
    public ResponseEntity<ProfileResponseDto> updateProfile(Principal principal,
                                                            @Valid @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {
        return ResponseEntity.ok(userService.updateProfile(principal.getName(), updateProfileRequestDto));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Changer son mot de passe")
    public ResponseEntity<Void> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequestDto req) {
        userService.changePassword(principal.getName(), req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @Operation(summary = "Supprimer son compte")
    public ResponseEntity<Void> deleteProfile(Principal principal) {
        userService.deleteAccount(principal.getName());
        return ResponseEntity.noContent().build();
    }
}
