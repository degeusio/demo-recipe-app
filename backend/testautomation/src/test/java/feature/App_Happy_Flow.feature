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
    Then status 401

    Scenario: Scenario-03: When posting a new recipe for a user with administrator privileges, we should be able to retrieve it back for any user
    Given path '/recipes'
    And request read("classpath:testdata/recipe.json")
    And header Content-Type = 'application/json'
    And header Authorization = call read('basic-auth.js') { username: 'adminuser', password: 'password' }
    When method POST
    Then status 201
    * def recipeUrl = responseHeaders['Location'][0]

    * url recipeUrl
    * print 'Getting recipe just created'
    And header Accept = 'application/json, text/plain, */*'
    When method GET
    Then status 200
    # testing only on snippet of response for now
    * match response.id == "#string"
    * match response.title == "Fresh Basil Pesto"

  Scenario: Scenario-04: A newly created recipe, an administrator must be able to update (add an ingredient) and delete it
    Given path '/recipes'
    And request read("classpath:testdata/cud_recipe.json")
    And header Content-Type = 'application/json'
    And header Authorization = call read('basic-auth.js') { username: 'adminuser', password: 'password' }
    When method POST
    Then status 201
    * def recipeUrl = responseHeaders['Location'][0]

    * print 'Getting and verifying the recipe created'
    Given url recipeUrl
    * print 'Getting recipe just created'
    And header Accept = 'application/json, text/plain, */*'
    When method GET
    Then status 200
    # testing only on snippet of response for now
    * match response.id == "#string"
    * match response.title == "Deleteme"
    * match response.ingredients == '#[7] #string'

    * print 'Updating the recipe just created with other payload'
    Given url recipeUrl
    And def recipe_id = response.id
    And request read("classpath:testdata/cud_recipe_update.json")
    And header Content-Type = 'application/json'
    And header Authorization = call read('basic-auth.js') { username: 'adminuser', password: 'password' }
    When method PUT
    Then status 200







