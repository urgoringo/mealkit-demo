package com.urgoringo.mealkit

import com.urgoringo.mealkit.scaffolding.ApplicationRunner
import org.springframework.beans.factory.annotation.Autowired

class RecipesCatalogSpec extends ApplicationSpecification {

    @Autowired
    ApplicationRunner app

    def setup() {
        app.deleteAllSubscriptions()
        app.deleteAllRecipes()
    }

    def "get available recipes"() {
        given: "system has following recipes available"
            """
        - Lemon Herb Chicken
        - Spicy Thai Basil Stir-Fry
        - Creamy Garlic Pasta
        """
            def expectedRecipes = [
                    "Lemon Herb Chicken",
                    "Spicy Thai Basil Stir-Fry",
                    "Creamy Garlic Pasta"
            ]
            app.havingRecipes(expectedRecipes)

        when: "customer queries available recipes"
            def recipes = app.getAllRecipes()

        then: "system returns these 3 recipes"
            recipes.size() == 3
            recipes[0].title() == expectedRecipes[0]
            recipes[1].title() == expectedRecipes[1]
            recipes[2].title() == expectedRecipes[2]
    }
}
