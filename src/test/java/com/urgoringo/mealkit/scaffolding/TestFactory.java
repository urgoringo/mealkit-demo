package com.urgoringo.mealkit.scaffolding;

import lombok.With;
import net.datafaker.Faker;

import java.util.List;

public class TestFactory {

    private static final Faker faker = new Faker();

    public static String anEmail() {
        return faker.internet().emailAddress();
    }

    public static String aPassword() {
        return faker.internet().password(8, 20, true, true, true);
    }

    public static String aCustomerEmail() {
        return faker.internet().emailAddress();
    }

    public static String anAddress() {
        return faker.address().streetAddress() + ", " +
               faker.address().zipCode() + " " + faker.address().city() + ", " +
               faker.address().country();
    }

    public static String aRecipeName() {
        return faker.food().dish();
    }

    public static RecipeBuilder aRecipe() {
        return new RecipeBuilder(aRecipeName(), List.of(), List.of());
    }

    @With
    public record RecipeBuilder(String title, List<String> ingredients, List<String> instructions) {
    }
}
