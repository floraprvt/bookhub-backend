package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.bo.dto.ChangePasswordRequestDto;
import fr.eni.bookhubbackend.entity.bo.dto.ProfileResponseDto;
import fr.eni.bookhubbackend.entity.bo.dto.UpdateProfileRequestDto;
import fr.eni.bookhubbackend.entity.bo.dto.UpdateUserRoleRequestDto;
import fr.eni.bookhubbackend.mapper.UserMapper;
import fr.eni.bookhubbackend.repository.LoanRepository;
import fr.eni.bookhubbackend.repository.RatingRepository;
import fr.eni.bookhubbackend.repository.ReservationRepository;
import fr.eni.bookhubbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final RatingRepository ratingRepository;

    public ProfileResponseDto getProfile(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        return userMapper.toProfileDto(user);
    }

    public ProfileResponseDto updateProfile (String email, UpdateProfileRequestDto updateProfileRequestDto){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        user.setFirstName(updateProfileRequestDto.getFirstName());
        user.setLastName(updateProfileRequestDto.getLastName());
        user.setPhone(updateProfileRequestDto.getPhone());
        return userMapper.toProfileDto(userRepository.save(user));
    }

    public void changePassword (String email, ChangePasswordRequestDto changePasswordRequestDto){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        if(!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(),  user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Le mot de passe actuel est incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        userRepository.save(user);
    }

    public Page<ProfileResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toProfileDto);
    }

    public ProfileResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        return userMapper.toProfileDto(user);
    }

    public ProfileResponseDto updateUserRole(Long id, UpdateUserRoleRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        user.setRole(dto.getRole());
        return userMapper.toProfileDto(userRepository.save(user));
    }

    public void deleteAccount(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        long activeLoans = loanRepository.countByUserAndIsReturnedFalse(user);
        if (activeLoans>0){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous ne pouvez pas supprimer votre compte tant que vous avez des emprunts en cours");
        }

        long activeReservations = reservationRepository.countByUser(user);
        if (activeReservations>0){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Vous ne pouvez pas supprimer votre compte tant que vous avez des réservatins en cours");
        }

        reservationRepository.deleteAllByUser(user);
        ratingRepository.deleteAllByUser(user);
        loanRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }


}
