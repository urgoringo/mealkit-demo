package com.urgoringo.mealkit

import com.urgoringo.mealkit.recipecatalog.domain.PricingCategory

import static com.urgoringo.mealkit.recipecatalog.domain.PricingCategory.*
import static com.urgoringo.mealkit.scaffolding.TestFactory.aRecipe

class RecipesPricingSpec extends ApplicationSpecification {

    def "recipe pricing is based on pricing category"() {
        given:
            "system has a recipe with pricing category MEDIUM"
            def recipeId = app.havingRecipe(aRecipe().withPricingCategory(MEDIUM)).id()

        when: "customer queries recipe"
            def recipe = app.getRecipe(recipeId)

        then: "system returns recipe pricing details"
            recipe.pricingCategory() == MEDIUM
            recipe.price().toDouble() == 10.5
    }
}