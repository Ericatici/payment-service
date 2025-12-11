Feature: Order Creation
  As a customer
  I want to create an order
  So that I can receive my products

  Scenario: Create an order successfully
    Given there is a customer with CPF "12345678900"
    And the customer has name "John Doe"
    When I create an order with the following items:
      | productId | productName | quantity | itemPrice |
      | PROD-001  | Product 1  | 2        | 10.50     |
      | PROD-002  | Product 2  | 1        | 25.00     |
    Then the order should be created successfully
    And the order should have status "WAITING_PAYMENT"
    And the order should have paymentStatus "PENDING"
    And the order should have a paymentId
    And the order should have a qrCodeData

  Scenario: Fail to create order with non-existent customer
    Given there is no customer with CPF "99999999999"
    When I try to create an order with CPF "99999999999"
    Then an InvalidOrderException should be thrown
    And the error message should contain "not found"
