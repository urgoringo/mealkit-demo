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

## Scenario: must select at least 2 recipes

Given customer has selected only 2 recipes
When customer tries to sign up for subscription
Then system returns 422 with validation error

## Scenario: delivery address is required

When customer tries to signup without delivery address
Then system returns 422 with validation error

## Scenario: subscription has delivery address

Given customer home address is: 
Pikk 15
10123 Tallinn
Estonia
When they signup for subscription 
Then subscription has customer's home address as delivery address

## Scenario: subscription has delivery day

Given today is 2025.11.19
And customer selects Monday as the delivery day 
When they signup for subscription
Then first order will be delivered on 2025.11.24


