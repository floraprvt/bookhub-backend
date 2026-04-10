package fr.eni.bookhubbackend.factory;

import fr.eni.bookhubbackend.entity.bo.Author;
import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class AuthorFactory {

    private static final Faker faker = new Faker();

    public static List<Author> createListAuthor(final int nbAuthor) {

        List<Author> authors = new ArrayList<>();

        for (int i = 0; i < nbAuthor; i++) {
            authors.add(Author.builder()
                    .id(faker.number().randomNumber())
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .build());
        }
        return authors;
    }
}
