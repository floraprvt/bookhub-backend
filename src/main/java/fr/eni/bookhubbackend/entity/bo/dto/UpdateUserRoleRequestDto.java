package fr.eni.bookhubbackend.entity.bo.dto;

import fr.eni.bookhubbackend.entity.enums.RoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequestDto {

    @NotNull
    private RoleEnum role;

}
