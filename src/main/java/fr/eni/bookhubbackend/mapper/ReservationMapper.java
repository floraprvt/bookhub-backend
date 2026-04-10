package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.entity.bo.Reservation;
import fr.eni.bookhubbackend.entity.dto.CreateReservationDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    CreateReservationDto toReservationDto(Reservation reservation);

    List<CreateReservationDto> toReservationDtos(List<Reservation> reservations);
}
