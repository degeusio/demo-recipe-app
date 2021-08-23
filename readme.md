# Recipe app

This project involves a simple recipe app, consisting of three separate components:
1. A Java/Spring Boot REST API backend
2. A PostgreSQL datastore, with schema changes under version control using Liquibase
3. An Angular/Typescript Single Page App frontend

Each component is built as docker container. This enables local integration testing (using the Karate framework) and allows for deployment on cloud solutions like Kubernetes on AWS/Azure. 

## Build and run the app locally

### Prerequisites
- JDK 11 installed
- maven installed
- docker engine is running (e.g. 'docker ps' works)
- internet access to obtain containers
- to run the build scripts: Linux/Unix OS (alternatively, emulator like GitBash (not tested))

### Script
This script builds and runs the app, consisting of three containers: a single page application container, a datastore container and a REST api container.
```shell
cwd=$(pwd)
echo "Building Single Page App (Angular) frontend container"
$cwd/frontend/build.sh
echo "Building recipe database container"
$cwd/backend/db/build.sh
echo "Building and testing Spring Boot Rest API app container"
$cwd/backend/build.sh
echo "Done. Visit the SPA at http://localhost:80, the api at http://localhost:8080/recipes or the database at with your (Postgres) Sql client of choice"
```

# Documentation and background

## Basis of the backend REST API
The Java/Spring Boot backend starter was created with Spring initializr (https://start.spring.io/), including the following dependencies:
  - web: to create REST backend;
  - security: to protect backend API with Spring Security;
  - data: for persistence with SQL-based backend;
  - validation: for bean validation of user input;
  - lombok: to reduce boilerplate in code.

## Architecture considerations
This section describes considerations taking into account for this app.

### Twelve factor app:
The configuration properties are externalized, see also [the 12-factor app](https://12factor.net/), i.e.:
- for the REST API: using Spring's native application.yml support;
- for the single page app: externalized using an injected backend API url;
- for the database: database connection details are injected from the environment

When porting to e.g. a cloud solution, these (sensitive) properties can be injected via encrypted files or key vault services.

### Layering & domain modelling
The domain model is kept very simple and consists of only 3 domain objects:
    - User: representing a user
    - UserRole: to account for the roles that a User may have. Now, only one admin user available, but that can be extended easily
    - Recipe: the Recipe representation itself

Currently, the domain objects serve as web request DTO (through Jackson marshalling), Java POJOs and @Entity's for persistence. For this simple start-up it suffices, but at some point it makes sense to split this up into different Java structures, each for a specific aspect (web integration, domain model and persistence).

The layering is kept simple as well: a web layer, a service layer and a persistence layer, recognizable by its package name.

### testautomation: end-to-end testing (E2E) before e.g. pushing a commit
For E2E testing, the [Karate test framework](https://github.com/intuit/karate) is used. Karate tests the REST API with Cucumber-like syntax, see e.g. [Bealdung](https://www.baeldung.com/karate-rest-api-testing).

The E2E test require the Rest API to be up and running (including the datasource). Further enhancements could include e.g. mocking other collaborating API services with wiremock.
  
### Authentication & Authorization
The app uses simple base64 authentication for now, with one admin user populated in the iam.user table as static data (as per the liquibase script). That user is:
- user: adminuser
- password: password

Other alternatives for are introducing forms based login, or e.g. using JWTs obtained from Authorization Service using OAuth2/OIDC to protect backend services (as 'ResourceServer' in OAuth2 terminology).
  
### Transaction management
Given we operate on a database and the app is intended for production, we have to account for transaction management. If not, we are simply using transaction management as default provided by the framework. 

For this app, a very straightforward approach is used:
    - web layer: no transactional boundary
    - service layer: create or reuse an existing boundary
    - persistence (repository) layer: must always participate in an existing transactional boundary.

This approach is implemented using Spring Frameworks declarative @Transactional support.

### REST API design
The following principles/designs are evaluated for the API design:
  - A simple design for Create, Read, Update and Delete (CRUD) operations for Recipe;
  - Operations logically follow the HTTP verb accordingly (i.e. POST, GET, PUT and DELETE respectively). For further enhancements and improvements, a good resource is e.g. the [Zalando API guidelines](http://zalando.github.io/restful-api-guidelines/);
  - Vulnerable operations modifying state (Change, Update, Delete) require authentication and authorization, implemented using Spring Security;
  - In principle, an administrator is trusted to not inject exploits as user input, but the API can be further shielded of that to cleanse administrator user input before e.g. persisting in the datastore to e.g. prevent XSS attacks;
  - Only full object updates are currently implemented. That is, all values passed to the PUT operation are modified. Later enhancements could allow for partial updates, by introducing e.g. a PATCH REST API method. See also the Zalando API guidelines;

### Security
  - Sensitive API operations (Create, Update, Delete) are protected and require authentication (AuthN) and authorization (AuthZ). 
  - User passwords are is not stored plain text (which is an absolute no no), but stored hashed and salted;
  - User access management is implemented using Spring Framework's AuthenticationProvider together with the Spring Security Framework;
  - Authorization is provided through Spring Security's global method security framework, e.g. by using the @PreAuthorize("hasRole('ADMINISTRATOR')") annotation. This is implemented in the service layer of the app, to e.g. allow for further enhancements and integrations with other systems (e.g. messaging systems or reporting systems) or federated authorization solutions relying on OAuth2/OIDC;
 - For now, authentication is simply implemented using basic authentication. Other means of authentication could (or should) be implemented later (forms-based login, SSO, JWTs through OAuth2/OIDC);
 - Of course, this app must be fronted with an SSL-offloading mechanism and only be accessible via httpS over the internet. This is handled very conveniently by cloud providers;
 - Specific security related http headers for serving static contents are added by the NGinX container, like e.g. 'content-security-policy' and 'referrer-policy'. 
 - The database connection used by the app is governed by the 'least privilege' principle. For instance, that connection has no rights to modify the iam.user table as it is select only. Even if the app would become compromised, the db connection cannot be used to alter that data.
 - User input validation is enabled through JSR-303 validation
 - The REST API functions fully stateless. E.g. no Http Session (JSessionID/Set-Cookie) is used. For true corporate deployments this would need to be aligned with the global security policy (e.g. when deployed behind certain load balancers);
 - The REST API operates a simple CORS configuration that allows for local setup, but configurable at runtime. Also this should be aligned with global security policy;
- Spring framework's out-of-the-box CSRF protection is disabled for now as it introduces state and the API is intentionally designed stateless to serve also non-browser clients. CSRF risk is somewhat mitigated as there is no session/cookie stored, yet, CSRF and replay attacks protection should be managed and be aligned with global security policy (e.g. with load balancers that maintain user sessions);
   
## The PostgreSQL datastore
  - database versioning is kept under strict code version control with liquibase
  - sensitive data, like user passwords, database urls and so on, can be injected at runtime from an environment file or through integration with a key vault.
  - in principle, each schema can be assigned with a different user that can access that schema. In this example app, the 'rcp_usr' is simply allowed to access both the 'iam' and 'rcp' schema;
  - the root user account is used to
    - create an 'app_mgr' user that is higher privileged user that can modify database objects
    - create an 'app_usr' user that is used by the app and only has privileges insofar needed to run the app.

# Limitations
- For a recipe, ingredients are modelled as list of string elements. Further enhancing could include making the model include units, unit of measure and a description to e.g. calculate necessary amounts on the fly (or e.g. integrate with auto-ordering of a recipe with some e-commerce platform)

- database versioning with liquibase, also the use of static data can be put under version control. E.g. to always start a new database with some list of recipes. 

- Database configuration settings like timeouts, failover/replication, data recovery need to be addressed further. Any database system offers a large amount of configuration that need to be optimized specifically for a particular targeted production runtime. This app uses the default settings, but highlights how these can be set using configuration properties.
  
