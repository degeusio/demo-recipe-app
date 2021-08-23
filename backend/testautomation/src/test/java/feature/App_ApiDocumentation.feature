Feature: The API should be documented

  Background:
    * url env.urls.app_base
    * header Accept = 'application/json'

    Scenario: Scenario-01: it should PUBLICLY expose the Swagger documentation for front-end rendering.
    Given path '/v2/api-docs'
    When method GET
    Then status 200
    * json response = response
    * match response.swagger == '2.0'

