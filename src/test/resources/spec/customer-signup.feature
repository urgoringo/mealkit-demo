Feature: Customer authentication

  Scenario: new customer signup
    When user signs up using their email and password
    Then system creates new customer with used credentials
    
  Scenario: customer login
    Given customer with email john.doe@example.com exists and has a subscripion
    When they log in using their email and password
    Then they can access their subscription

  Scenario: duplicate email
    Given customer with email: jane.doe@example.com already exists
    When customer tries to signup using jane.doe@example.com
    Then system returns 422 with validation error
