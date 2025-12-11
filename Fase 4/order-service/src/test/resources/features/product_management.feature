Feature: Product Management
  As a system administrator
  I want to manage products
  So that I can maintain the product catalog

  Scenario: Create a new product successfully
    Given I have a product with name "Hamburger", description "Delicious hamburger", price 25.00 and category "LANCHE"
    When I create the product
    Then the product should be created successfully
    And the product should have an ID
    And the product name should be "Hamburger"

  Scenario: Find product by ID
    Given a product with ID "PROD-001" exists in the system
    When I search for the product with ID "PROD-001"
    Then the product should be found
    And the product ID should be "PROD-001"

  Scenario: Product not found when searching with invalid ID
    Given no product with ID "PROD-999" exists in the system
    When I search for the product with ID "PROD-999"
    Then an error should occur indicating product not found

