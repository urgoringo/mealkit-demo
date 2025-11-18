# Signup for subscription

## Scenario: happy path

Given customer has no existing subscription
And 3 recipes are available in the system
When customer chooses these recipes for upcoming order
Then system creates new subscription with upcoming order that contains these 3 recipes
