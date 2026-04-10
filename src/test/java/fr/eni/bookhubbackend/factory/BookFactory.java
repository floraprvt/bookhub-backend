package fr.eni.bookhubbackend.factory;

import fr.eni.bookhubbackend.entity.bo.Author;
import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.bo.Category;
import net.datafaker.Faker;

import java.util.List;

import static fr.eni.bookhubbackend.factory.AuthorFactory.createListAuthor;
import static fr.eni.bookhubbackend.factory.CategoryFactory.createListCategory;

public class BookFactory {

    private static final Faker faker = new Faker();

    public static Book createBook() {
        List<Author> authors = createListAuthor(faker.number().numberBetween(1, 3));
        List<Category> categories = createListCategory(faker.number().numberBetween(1, 2));

        return Book.builder()
                .title(faker.book().title())
                .isbn(faker.code().isbn13())
                .author(authors)
                .category(categories)
                .description(faker.lorem().paragraph())
                .date(faker.timeAndDate().birthday())
                .isAvailable(faker.random().nextBoolean())
                .build();

    }
}
