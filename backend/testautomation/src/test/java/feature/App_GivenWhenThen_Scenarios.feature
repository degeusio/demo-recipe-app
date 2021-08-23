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

  Scenario: Scenario-04: An administrator must be able to create, update and delete a recipe.
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
    # testing changes in response for now
    * match response.id == recipe_id
    * match response.title == "Deleteme"
    * match response.vegetarian == false
    * match response.instructions == "Another lorem."
    * match response.ingredients == '#[8] #string'

    * print 'Deleting the recipe just created'
    Given url recipeUrl
    And header Authorization = call read('basic-auth.js') { username: 'adminuser', password: 'password' }
    When method DELETE
    Then status 200

    * print 'After deletion, API should return Not Found (404) for the deleted recipe'
    Given url recipeUrl
    When method GET
    Then status 404

  Scenario: Scenario-05: An administrator must not be able to create an invalid recipe missing ingredients
    Given path '/recipes'
    And request read("classpath:testdata/recipe_gourmet_mushroom_risotto_bad.json")
    And header Content-Type = 'application/json'
    And header Authorization = call read('basic-auth.js') { username: 'adminuser', password: 'password' }
    When method POST
    Then status 400
    * match response == "create.candidate.ingredients: must not be null"

  Scenario: Scenario-06: An administrator must not be able to create an valid additional recipe including ingredients
    Given path '/recipes'
    And request read("classpath:testdata/recipe_gourmet_mushroom_risotto_good.json")
    And header Content-Type = 'application/json'
    And header Authorization = call read('basic-auth.js') { username: 'adminuser', password: 'password' }
    When method POST
    Then status 201





