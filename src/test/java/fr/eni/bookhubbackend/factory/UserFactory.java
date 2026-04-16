package fr.eni.bookhubbackend.factory;

import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.enums.RoleEnum;
import net.datafaker.Faker;

public class UserFactory {

    private static final Faker faker = new Faker();

    public static User createUser() {
        return User.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password(12, 20))
                .phone(faker.number().digits(10))
                .role(RoleEnum.USER)
                .build();
    }
}
