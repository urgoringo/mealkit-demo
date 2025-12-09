package com.urgoringo.mealkit

import com.urgoringo.mealkit.scaffolding.ApplicationRunner
import org.springframework.beans.factory.annotation.Autowired

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
}
