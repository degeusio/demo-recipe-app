Feature: Test scenarios for the app.

  Background:
    * url env.urls.app_base
    * header Accept = 'application/json'

    Scenario: Scenario-01: When visiting list of recipes as visitor, expect to see a list of recipes.
    Given path '/recipes'
    When method GET
    Then status 200

    Scenario: Scenario-02: When posting a recipe as visitor (not admin), we should get forbidden (403) for lack of privileges
    Given path '/recipes'
    And request read("classpath:testdata/recipe.json")
    And header Content-Type = 'application/json'
    When method POST
    Then status 403
