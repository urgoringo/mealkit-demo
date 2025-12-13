package com.urgoringo.mealkit

class IngredientCatalogSpec extends ApplicationSpecification {

    def "gets ingredient by name"() {
        given: "system has ingredient 'chicken breast'"
            def createdIngredient = app.havingIngredient("chicken breast")

        when: "customer queries ingredient by name"
            def ingredient = app.findIngredient("chicken breast")

        then: "system returns this ingredient"
            ingredient.name() == "chicken breast"
            ingredient.id() != null
            ingredient.id() == createdIngredient.id()
    }

}
