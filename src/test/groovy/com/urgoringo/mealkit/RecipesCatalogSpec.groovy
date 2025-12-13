package com.urgoringo.mealkit

import com.urgoringo.mealkit.scaffolding.ApplicationRunner
import org.springframework.beans.factory.annotation.Autowired

import static com.urgoringo.mealkit.scaffolding.TestFactory.aRecipe

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
            def expectedIngredients = ["chicken breast", "lemon", "fresh herbs"]
            def expectedInstructions = ["Season the chicken with herbs", "Grill for 15 minutes", "Serve with lemon"]
            def recipeId = app.havingRecipe(aRecipe().withTitle(recipeTitle).withIngredients(expectedIngredients).withInstructions(expectedInstructions)).id()

        when: "customer queries available recipes"
            def recipe = app.getRecipe(recipeId)

        then: "system returns this recipe"
            recipe.title() == recipeTitle
            recipe.ingredients() == expectedIngredients
            recipe.instructions() == expectedInstructions
    }
}