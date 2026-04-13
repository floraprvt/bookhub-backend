package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.bo.dto.ChangePasswordRequestDto;
import fr.eni.bookhubbackend.entity.bo.dto.ProfileResponseDto;
import fr.eni.bookhubbackend.entity.bo.dto.UpdateProfileRequestDto;
import fr.eni.bookhubbackend.mapper.UserMapper;
import fr.eni.bookhubbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public ProfileResponseDto getProfile(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return userMapper.toProfileDto(user);
    }

    public ProfileResponseDto updateProfile (String email, UpdateProfileRequestDto updateProfileRequestDto){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setFirstName(updateProfileRequestDto.getFirstName());
        user.setLastName(updateProfileRequestDto.getLastName());
        user.setPhone(updateProfileRequestDto.getPhone());
        return userMapper.toProfileDto(userRepository.save(user));
    }

    public void changePassword (String email, ChangePasswordRequestDto changePasswordRequestDto){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if(!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(),  user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current Password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        userRepository.save(user);
    }

    public void deleteAccount(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
    }


}
