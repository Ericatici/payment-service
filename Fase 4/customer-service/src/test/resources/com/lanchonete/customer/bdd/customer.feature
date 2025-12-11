Feature: Customer Management
  As a system
  I want to manage customer information
  So that I can register, query and update customer data

  Scenario: Create a new customer successfully
    Given I have a valid customer with CPF "12345678900", name "John Doe" and email "john.doe@example.com"
    When I create the customer
    Then the customer should be created successfully with status 201
    And the response should contain CPF "12345678900", name "John Doe" and email "john.doe@example.com"

  Scenario: Try to create a customer with existing CPF
    Given a customer with CPF "12345678900" already exists
    When I try to create a customer with CPF "12345678900", name "Jane Doe" and email "jane.doe@example.com"
    Then I should receive an error with status 400
    And the error message should indicate that the customer already exists

  Scenario: Find customer by CPF successfully
    Given a customer with CPF "98765432100" exists in the system
    When I search for the customer with CPF "98765432100"
    Then the customer should be found with status 200
    And the response should contain the customer information

  Scenario: Try to find a non-existent customer
    Given no customer with CPF "99999999999" exists in the system
    When I search for the customer with CPF "99999999999"
    Then I should receive a not found response with status 404

  Scenario: Update customer successfully
    Given a customer with CPF "11122233344" exists in the system
    When I update the customer with CPF "11122233344" with name "Updated Name" and email "updated@example.com"
    Then the customer should be updated successfully with status 202
    And the response should contain the updated information

