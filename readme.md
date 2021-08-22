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
  
- transaction management
  given we operate on a database and the app is intended for production, we have to account for transaction management. If not, we are simply using transaction management as default provided by the framework. For this app, we use a very simple and consistent approach:
    - web layer: no transactional boundary
    - service layer: create or reuse an existing boundary
    - persistence (repository) layer: must always participate in an existing transactional boundary.
  this approach is implemented using Spring Frameworks declarative @Transactional support.

- REST API design 
  - simple design for Create, Read, Update and Delete (CRUD) operations for Recipe
  - operations logically follow the HTTP verb accordingly (i.e. POST, GET, PUT and DELETE respectively)  
  - further enhancements and improvements, a good resource is e.g. the [Zalando API guidelines](http://zalando.github.io/restful-api-guidelines/)
  - Vulnerable operations modifying state (CUD) require authentication and authorization, implemented using Spring Security.
  - In principle, an administrator is trusted to not inject exploits as user input but the API can be further shielded of that to cleanse administrator user input before e.g. persisting in the datastore.
  - only full object updates are currently implemented. That is, all values passed to the PUT operation are modified. Later enhancements could allow for partial updates, by introducing e.g. a PATCH REST API method. See also the Zalando API guidelines.

- Security
  - Sensitive operations require authentication (AuthN) and authorization (AuthZ). 
  - Currently, the app is using a statically added user who has a role of Administrator and a password of 'password'. The password is not stored plain text, but hashed and salted via BCrypt.
  - The user table database is integrated via the 'UserDetails' contract with AuthenticationProvider within the Spring (Security) Framework
  - Authorization is provided through Spring Security's global method security framework, e.g. by using the @PreAuthorize("hasRole('ADMINISTRATOR')") annotation. This is implemented in the service layer of the app, to e.g. allow for further enhancements and integrations with other systems (e.g. messaging systems or reporting systems) or federated authorization solutions relying on OAuth2/OIDC.
  
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
