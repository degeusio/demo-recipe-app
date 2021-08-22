# Recipe app

## Backend
### Starter of the project
- Starter of backend api setup created with Spring initializr (https://start.spring.io/), including dependencies:
  - web: to create REST backend
  - security: to protect backend API with Spring Security
  - data: for persistence with SQL-based backend
  - validation: for bean validation of user input
  - lombok: to reduce boilerplate in code.

### Architecture
- configuration properties are externalized, see also [the 12-factor app](https://12factor.net/)
  
- testautomation
  using [Karate test framework](https://github.com/intuit/karate) to test the REST API with Cucumber-like syntax, see e.g. [Bealdung](https://www.baeldung.com/karate-rest-api-testing)
  - uses JUnit4 approach for time being
  - may be enhanced by bringing up and tearing down a wiremock server to mock collaborating other API services.
  
- Authentication & Authorization
Using simple base64 authentication for now. We can think of other forms of login later, e.g. using JWTs obtained from Authorization Service using OAuth2/OIDC.
  

- database
  - least privilege principle is applied through the creation of a specific, low privilege app user
  - in this example, the user that the app uses has no privileges to modify the IAM tables. Even if the app would become compromised, changing that data is prevented. E.g. the 'app_usr' has only select privileges on the iam.* table objects. 
  - database versioning is kept under strict code version control with liquibase
  - sensitive data, like user passwords, database urls and so on, can be injected at runtime from an environment file or through integration with a key vault.
  - in principle, each schema can be assigned with a different user that can access that schema. In this example app, the 'rcp_usr' is simply allowed to access both the 'iam' and 'rcp' schema;
  - the root user account is used to
    - create an 'app_mgr' user that is higher privileged user that can modify database objects
    - create an 'app_usr' user that is used by the app and only has privileges insofar needed to run the app.

### Limitations
- For a recipe, ingredients are modelled as list of string elements. Further enhancing could include making the model include units, unit of measure and a description to e.g. calculate necessary amounts on the fly (or e.g. integrate with auto-ordering of a recipe with some e-commerce platform)

- database versioning
  - with liquibase, also the use of static data can be put under version control. E.g. to always start a new database with some list of recipes. 
  
### Production and other consideration 
- Database configuration settings like timeouts, failover/replication, data recovery need to be addressed further. Any database system offers a large amount of configuration that need to be optimized specifically for a particular targeted production runtime. This app uses the default settings, but highlights how these can be set using configuration properties.
