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
- 

### Production and other consideration 
- Database configuration settings like timeouts, failover/replication, data recovery need to be addressed further. Any database system offers a large amount of configuration that need to be optimized specifically for a particular targeted production runtime. This app uses the default settings, but highlights how these can be set using configuration properties.
