Feature: Consult Payment Status
  As a payment service
  I want to consult the payment status of an order
  So that I can provide accurate payment information

  Scenario: Get payment status for an approved order
    Given an order with id "1" exists
    And the order has payment status "APPROVED"
    When I consult the payment status for order "1"
    Then the payment status should be "APPROVED"
    And the MercadoPago API should not be consulted

  Scenario: Get payment status for a pending order
    Given an order with id "1" exists
    And the order has payment status "PENDING"
    And the order has payment id "mp-payment-123"
    And MercadoPago returns an approved payment for "mp-payment-123"
    When I consult the payment status for order "1"
    Then the payment status should be "APPROVED"
    And the MercadoPago API should be consulted once
    And the order payment status should be updated

  Scenario: Get payment status for a non-existent order
    Given an order with id "999" does not exist
    When I consult the payment status for order "999"
    Then an OrderNotFoundException should be thrown
    And the error message should contain "Order with id 999 not found"

