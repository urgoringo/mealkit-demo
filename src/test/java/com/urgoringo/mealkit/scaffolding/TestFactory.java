package com.urgoringo.mealkit.scaffolding;

import lombok.With;
import net.datafaker.Faker;

import java.time.DayOfWeek;
import java.util.List;

import static java.time.DayOfWeek.WEDNESDAY;

public class TestFactory {

    private static final Faker faker = new Faker();

    public static String anEmail() {
        return faker.internet().emailAddress();
    }

    public static String aPassword() {
        return faker.internet().password(8, 20, true, true, true);
    }

    public static String anAddress() {
        return faker.address().streetAddress() + ", " +
               faker.address().zipCode() + " " + faker.address().city() + ", " +
               faker.address().country();
    }

    private static String aRecipeName() {
        return faker.food().dish();
    }

    public static RecipeBuilder aRecipe() {
        return new RecipeBuilder(aRecipeName(), List.of(), List.of());
    }

    public static SubscriptionBuilder aSubscription() {
        return new SubscriptionBuilder(List.of(), anAddress(), WEDNESDAY);
    }

    public static IngredientBuilder anIngredient() {
        return new IngredientBuilder("ingredient", "1", "piece");
    }


   @With
    public record RecipeBuilder(String title, List<String> instructions, List<IngredientBuilder> ingredients) {
    }

    @With
    public record IngredientBuilder(String name, String quantity, String unit) {
    }

    @With
    public record SubscriptionBuilder(List<Long> recipeIds, String deliveryAddress,
                                      DayOfWeek deliveryDay) {
    }

}
