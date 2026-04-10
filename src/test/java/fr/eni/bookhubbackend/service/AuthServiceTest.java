package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.bo.dto.AuthResponseDto;
import fr.eni.bookhubbackend.entity.bo.dto.LoginRequestDto;
import fr.eni.bookhubbackend.entity.bo.dto.RegisterRequestDto;
import fr.eni.bookhubbackend.entity.enums.RoleEnum;
import fr.eni.bookhubbackend.repository.UserRepository;
import fr.eni.bookhubbackend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("ahmed@bookhub.fr")
                .password("$2a$12$hashedPassword")
                .firstName("Ahmed")
                .lastName("Dupont")
                .role(RoleEnum.USER)
                .build();

        registerRequest = new RegisterRequestDto();
        registerRequest.setEmail("ahmed@bookhub.fr");
        registerRequest.setPassword("Azerty123!@#");
        registerRequest.setFirstName("Ahmed");
        registerRequest.setLastName("Dupont");

        loginRequest = new LoginRequestDto();
        loginRequest.setEmail("ahmed@bookhub.fr");
        loginRequest.setPassword("Azerty123!@#");
    }

    // ---- REGISTER ----

    @Test
    void register_success() {
        // GIVEN
        when(userRepository.existsByEmail("ahmed@bookhub.fr")).thenReturn(false);
        when(passwordEncoder.encode("Azerty123!@#")).thenReturn("$2a$12$hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("fake.jwt.token");

        // WHEN
        AuthResponseDto response = authService.register(registerRequest);

        // THEN
        assertThat(response.getToken()).isEqualTo("fake.jwt.token");
        assertThat(response.getEmail()).isEqualTo("ahmed@bookhub.fr");
        assertThat(response.getRole()).isEqualTo("USER");

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Azerty123!@#");
    }

    @Test
    void register_emailAlreadyTaken_throwsConflict() {
        // GIVEN
        when(userRepository.existsByEmail("ahmed@bookhub.fr")).thenReturn(true);

        // WHEN / THEN
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                });

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_passwordIsHashed() {
        // GIVEN
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        // WHEN
        authService.register(registerRequest);

        // THEN — le mot de passe en clair ne doit jamais être sauvegardé
        verify(passwordEncoder).encode("Azerty123!@#");
        verify(userRepository).save(argThat(u ->
                u.getPassword().equals("$2a$12$hashedPassword")
        ));
    }

    // ---- LOGIN ----

    @Test
    void login_success() {
        // GIVEN
        when(userRepository.findByEmail("ahmed@bookhub.fr")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Azerty123!@#", "$2a$12$hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("fake.jwt.token");

        // WHEN
        AuthResponseDto response = authService.login(loginRequest);

        // THEN
        assertThat(response.getToken()).isEqualTo("fake.jwt.token");
        assertThat(response.getEmail()).isEqualTo("ahmed@bookhub.fr");
    }

    @Test
    void login_userNotFound_throwsUnauthorized() {
        // GIVEN
        when(userRepository.findByEmail("ahmed@bookhub.fr")).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_wrongPassword_throwsUnauthorized() {
        // GIVEN
        when(userRepository.findByEmail("ahmed@bookhub.fr")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Azerty123!@#", "$2a$12$hashedPassword")).thenReturn(false);

        // WHEN / THEN
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_wrongEmail_andWrongPassword_sameErrorMessage() {
        // Cas 1 : email introuvable
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ResponseStatusException ex1 = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(loginRequest)
        );

        // Cas 2 : mauvais mot de passe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        ResponseStatusException ex2 = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(loginRequest)
        );

        assertThat(ex1.getReason()).isEqualTo(ex2.getReason());
    }
}