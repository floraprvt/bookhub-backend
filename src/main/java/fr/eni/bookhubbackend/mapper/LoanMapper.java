package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.entity.bo.Loan;
import fr.eni.bookhubbackend.entity.bo.dto.LoanResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(target = "late", expression = "java(!loan.getIsReturned() && loan.getReturnDate().isBefore(java.time.LocalDate.now()))")
    LoanResponseDto toDto(Loan loan);
}