package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.bo.dto.AuthResponseDto;
import fr.eni.bookhubbackend.entity.bo.dto.LoginRequestDto;
import fr.eni.bookhubbackend.entity.bo.dto.RegisterRequestDto;
import fr.eni.bookhubbackend.entity.enums.RoleEnum;
import fr.eni.bookhubbackend.repository.UserRepository;
import fr.eni.bookhubbackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDto register(RegisterRequestDto req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken");
        }
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .role(RoleEnum.USER)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponseDto(token, user.getRole().name(), user.getEmail());
    }

    public AuthResponseDto login(LoginRequestDto req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Id's not matching"));


        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Id's not matching");
        }
        String token = jwtService.generateToken(user);
        return new AuthResponseDto(token, user.getRole().name(), user.getEmail());
    }
}
