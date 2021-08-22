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
  

### Production and other consideration 
- Database configuration settings like timeouts, failover/replication, data recovery need to be addressed further. Any database system offers a large amount of configuration that need to be optimized specifically for a particular targeted production runtime. This app uses the default settings, but highlights how these can be set using configuration properties.
