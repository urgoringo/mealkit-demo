package com.urgoringo.mealkit.scaffolding;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.Unit;
import com.urgoringo.mealkit.subscription.domain.UpcomingOrder;
import lombok.With;
import net.datafaker.Faker;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
        return new RecipeBuilder(aRecipeName(), List.of(), List.of(anIngredient()));
    }

    public static SubscriptionBuilder aSubscription() {
        return new SubscriptionBuilder(List.of(), anAddress(), WEDNESDAY);
    }

    public static IngredientBuilder anIngredient() {
        return new IngredientBuilder("ingredient", "1", Unit.PIECE);
    }

    public static UpcomingOrderBuilder anUpcomingOrder() {
        return new UpcomingOrderBuilder(
            Id.of(1L),
            List.of(Id.of(1L), Id.of(2L), Id.of(3L)),
            LocalDate.now().plusDays(7)
        );
    }


   @With
    public record RecipeBuilder(String title, List<String> instructions, List<IngredientBuilder> ingredients) {
    }

    @With
    public record IngredientBuilder(String name, String quantity, Unit unit) {
    }

    @With
    public record SubscriptionBuilder(List<Long> recipeIds, String deliveryAddress,
                                      DayOfWeek deliveryDay) {
    }

    @With
    public record UpcomingOrderBuilder(Id<UpcomingOrder> id,
                                       List<Id<Recipe>> recipeIds,
                                       LocalDate deliveryDate) {
        
        public UpcomingOrder build() {
            return new UpcomingOrder(id, recipeIds, deliveryDate);
        }
    }

}
