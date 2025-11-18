# Signup for subscription

## Scenario: happy path

Given customer has no existing subscription
And 3 recipes are available in the system
When customer chooses these recipes for upcoming order
Then system creates new subscription with upcoming order that contains these 3 recipes

## Scenario: duplicate email

Given customer with email: jane.doe@example.com already exists
When customer tries to signup subsciption using jane.doe@example.com
Then system returns 422 with validation error
