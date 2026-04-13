package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.dto.BookDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",  uses = { AuthorMapper.class, CategoryMapper.class })
public interface BookMapper {

    BookDto toBookDto(Book book);

    List<BookDto> toBookDto(List<Book> books);
}
