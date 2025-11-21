Feature: Preselect recipes for upcoming order

  Scenario: select recipes for the next upcoming order
    Given a subscription exists where upcoming order has delivery date 2025.11.24
    When current day becomes 2025.11.21
    Then system adds new upcoming order with delivery date 2025.12.01
    
