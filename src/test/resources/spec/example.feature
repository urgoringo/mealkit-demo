Feature: Example

  Scenario: Basic application startup
    Given the Mealkit application is running
    When I check the application status
    Then the application should be healthy
