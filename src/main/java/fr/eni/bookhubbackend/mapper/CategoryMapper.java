package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.entity.bo.Category;
import fr.eni.bookhubbackend.entity.dto.CategoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);
}
