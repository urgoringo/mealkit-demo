# Signup for subscription


## Scenario: happy path

```
Given customer has no existing subscription
And recipes: 
  1. Lemon Herb Chicken
  2. Spicy Thai Basil Stir-Fry
  3. Creamy Garlic Pasta
are available in the system
When customer chooses these recipes for upcoming order
Then system creates new subscription with upcoming order that contains these recipes 
```
