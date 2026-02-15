package com.urgoringo.mealkit.mealkit61.mealkit2

import com.urgoringo.mealkit.ApplicationSpecification

import static com.urgoringo.mealkit.recipecatalog.domain.Unit.*
import static com.urgoringo.mealkit.scaffolding.TestFactory.aRecipe
import static com.urgoringo.mealkit.scaffolding.TestFactory.anIngredient

class RecipesCatalogSpec extends ApplicationSpecification {

    def "get available recipes"() {
        given: "system has following recipes available"
            def expectedRecipes = [
                    "Lemon Herb Chicken",
                    "Spicy Thai Basil Stir-Fry",
                    "Creamy Garlic Pasta"
            ]
            app.havingRecipes(expectedRecipes)

        when: "customer queries available recipes"
            def recipes = app.getAllRecipes()

        then: "system returns these 3 recipes"
            recipes*.title().containsAll(expectedRecipes)
    }

    def "recipe has ingredients and recipe instruction steps"() {
        given: "system has following recipe available"
            def recipeTitle = "Lemon Herb Chicken"
            def expectedInstructions = ["Season the chicken with herbs", "Grill for 15 minutes", "Serve with lemon"]
            def recipeId = app.havingRecipe(aRecipe()
                    .withTitle(recipeTitle)
                    .withInstructions(expectedInstructions)
                    .withIngredients([
                            anIngredient().withName("chicken breast").withQuantity("500").withUnit(GRAM),
                            anIngredient().withName("lemon").withQuantity("1").withUnit(PIECE),
                            anIngredient().withName("fresh herbs").withQuantity("1").withUnit(CUP)
                    ])
            ).id()

        when: "customer queries available recipes"
            def recipe = app.getRecipe(recipeId)

        then: "system returns this recipe"
            recipe.title() == recipeTitle
            recipe.ingredients().size() == 3
            recipe.ingredients()[0].name() == "chicken breast"
            recipe.ingredients()[1].name() == "lemon"
            recipe.ingredients()[2].name() == "fresh herbs"
            recipe.instructions() == expectedInstructions
    }

    def "each recipe ingredient has quantity and unit (supported units are: g, piece, cup)"() {
        given: "system has following recipe available"
            def recipeTitle = "Lemon Herb Chicken"
            def recipeId = app.havingRecipe(aRecipe()
                    .withTitle(recipeTitle)
                    .withIngredients(
                            [
                                    anIngredient().withName("minced chicken breast").withQuantity("300").withUnit(GRAM),
                                    anIngredient().withName("lemon").withQuantity("1").withUnit(PIECE),
                                    anIngredient().withName("fresh herbs").withQuantity("1/2").withUnit(CUP)
                            ]
                    )
            ).id()

        when: "customer queries recipe ingredients"
            def recipe = app.getRecipe(recipeId)

        then: "system returns this recipe ingredients with quantities and units"
            recipe.ingredients().size() == 3
            recipe.ingredients()[0].name() == "minced chicken breast"
            recipe.ingredients()[0].quantity() == "300"
            recipe.ingredients()[0].unit() == GRAM
            recipe.ingredients()[1].name() == "lemon"
            recipe.ingredients()[1].quantity() == "1"
            recipe.ingredients()[1].unit() == PIECE
            recipe.ingredients()[2].name() == "fresh herbs"
            recipe.ingredients()[2].quantity() == "1/2"
            recipe.ingredients()[2].unit() == CUP
    }
}