FROM openjdk:14

HEALTHCHECK --interval=30s --start-period=60s --retries=2 --timeout=5s CMD curl -fs http://localhost:${SERVER_PORT:-8080}${CONTEXT_PATH:-}/recipes

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
