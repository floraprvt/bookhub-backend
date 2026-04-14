package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.entity.dto.RatingDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    RatingDto toRatingDto(Rating rating);
}
