package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.bo.dto.AuthResponseDto;
import fr.eni.bookhubbackend.entity.bo.dto.RegisterRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // RegisterRequestDto → User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "phone", ignore = true)
    User toEntity(RegisterRequestDto dto);

    // User → AuthResponseDto
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    AuthResponseDto toDto(User user);
}