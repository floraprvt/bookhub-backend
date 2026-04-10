package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.entity.bo.Author;
import fr.eni.bookhubbackend.entity.dto.AuthorDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDto toAuthorDto(Author author);
}
