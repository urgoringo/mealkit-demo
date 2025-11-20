Feature: Recipes catalog

  Scenario: get available recipes
    Given system has following recipes available
      """
      - Lemon Herb Chicken
      - Spicy Thai Basil Stir-Fry
      - Creamy Garlic Pasta
      """
    When customer queries available recipes
    Then system returns these 3 recipes
