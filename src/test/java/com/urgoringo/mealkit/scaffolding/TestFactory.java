package com.urgoringo.mealkit.scaffolding;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.PricingCategory;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.Unit;
import com.urgoringo.mealkit.subscription.domain.Order;
import com.urgoringo.mealkit.subscription.domain.OrderStatus;
import com.urgoringo.mealkit.subscription.domain.PendingOrder;
import com.urgoringo.mealkit.subscription.domain.UpcomingOrder;
import lombok.With;
import net.datafaker.Faker;
import org.jspecify.annotations.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static com.urgoringo.mealkit.recipecatalog.domain.PricingCategory.MEDIUM;
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
        return new RecipeBuilder(aRecipeName(), List.of(), List.of(anIngredient()), MEDIUM);
    }

    public static SubscriptionBuilder aSubscription() {
        return new SubscriptionBuilder(List.of(), anAddress(), WEDNESDAY);
    }

    public static IngredientBuilder anIngredient() {
        return new IngredientBuilder("ingredient", "1", Unit.PIECE);
    }

    public static PendingOrderBuilder anPendingOrder() {
        return new PendingOrderBuilder(
            Id.of(1L),
            List.of(Id.of(1L), Id.of(2L), Id.of(3L)),
            LocalDate.now().plusDays(7)
        );
    }

   @With
    public record RecipeBuilder(String title, List<String> instructions, List<IngredientBuilder> ingredients, PricingCategory pricingCategory) {
    }

    @With
    public record IngredientBuilder(String name, String quantity, Unit unit) {
    }

    @With
    public record SubscriptionBuilder(List<Long> recipeIds, String deliveryAddress,
                                      DayOfWeek deliveryDay) {
    }

    @With
    public record PendingOrderBuilder(Id<Order> id,
                                      List<Id<Recipe>> recipeIds,
                                      LocalDate deliveryDate) {
        
        public PendingOrder build() {
            return new PendingOrder(id, recipeIds, deliveryDate);
        }
    }

}
