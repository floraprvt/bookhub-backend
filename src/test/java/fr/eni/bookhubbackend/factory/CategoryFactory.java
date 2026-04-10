package fr.eni.bookhubbackend.factory;


import fr.eni.bookhubbackend.entity.bo.Category;
import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class CategoryFactory {

    private static final Faker faker = new Faker();

    public static List<Category> createListCategory(int nbCategory) {
        List<Category> categoryList = new ArrayList<>();

        for (int i = 0; i < nbCategory; i++) {
            categoryList.add(Category.builder()
                    .id(faker.number().randomNumber())
                    .name(faker.book().genre())
                    .build());
        }
        return categoryList;
    }

}
