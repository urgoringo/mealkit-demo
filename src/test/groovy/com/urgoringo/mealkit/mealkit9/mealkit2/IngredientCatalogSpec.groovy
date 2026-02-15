package com.urgoringo.mealkit.mealkit9.mealkit2

import com.urgoringo.mealkit.ApplicationSpecification

class IngredientCatalogSpec extends ApplicationSpecification {

    def "gets ingredient by name"() {
        given: "system has ingredient 'chicken breast'"
            def createdIngredient = app.havingIngredient("chicken breast")

        when: "customer queries ingredient by name"
            def ingredient = app.findIngredient("chicken breast").expectSuccess()

        then: "system returns this ingredient"
            ingredient.name() == "chicken breast"
            ingredient.id() != null
            ingredient.id() == createdIngredient.id()
    }

    def "ingredient not found"() {
        when: "customer queries ingredient by name"
            def response = app.findIngredient("nonexistent ingredient")

        then: "system returns 404"
            response.expectError() == 404
    }

}
