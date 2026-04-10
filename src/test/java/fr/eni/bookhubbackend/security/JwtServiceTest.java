package fr.eni.bookhubbackend.security;

import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    // Clé Base64 de 32 octets valide pour les tests
    private static final String TEST_SECRET =
            "VGhpcklzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JCb29rSHViMjAyNiE=";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Injecte les @Value sans Spring context
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);

        user = User.builder()
                .id(1L)
                .email("ahmed@bookhub.fr")
                .firstName("Ahmed")
                .lastName("Dupont")
                .password("hashed")
                .role(RoleEnum.USER)
                .build();
    }

    @Test
    void generateToken_containsEmail() {
        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractEmail(token)).isEqualTo("ahmed@bookhub.fr");
    }

    @Test
    void generateToken_isValidForSameUser() {
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValid_wrongUser_returnsFalse() {
        String token = jwtService.generateToken(user);

        User otherUser = User.builder()
                .email("other@bookhub.fr")
                .password("hashed")
                .role(RoleEnum.USER)
                .build();

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        // GIVEN — génère un token déjà expiré
        ReflectionTestUtils.setField(jwtService, "expiration", -1L);
        String expiredToken = jwtService.generateToken(user);

        // Remet l'expiration normale pour que isTokenValid puisse parser sans problème de config
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);

        // WHEN / THEN
        assertThat(jwtService.isTokenValid(expiredToken, user)).isFalse();
    }

    @Test
    void extractEmail_returnsCorrectEmail() {
        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractEmail(token)).isEqualTo("ahmed@bookhub.fr");
    }
}