#!/bin/bash
mvn clean package
CONTEXT_PATH=""

echo ">> Building docker image"
echo "Set CONTEXT_PATH for internal docker image up check: $CONTEXT_PATH"
DATE=$(date '+%Y-%m-%d')
GITVERSION=$(git rev-parse --short=8 HEAD)
VERSION="$DATE-build-$GITVERSION"

IMAGE="degeus.io/recipe-app-api:$VERSION"
IMAGE_LATEST="degeus.io/recipe-app-api:latest"
docker build -t $IMAGE --build-arg CONTEXT_PATH=$CONTEXT_PATH api/.
docker tag $IMAGE $IMAGE_LATEST

RECIPE_DB_IMAGE="degeus.io/recipe-app-db:latest"
RECIPE_WEB_IMAGE="degeus.io/recipe-app-spa:latest"
echo "Setting up environment for test automation..."
echo "Using app version: $IMAGE"
echo "Using db version: $RECIPE_DB_IMAGE"
export IMAGE=$IMAGE DB_IMAGE=$RECIPE_DB_IMAGE WEB_IMAGE=$RECIPE_WEB_IMAGE; docker-compose -f api/docker/release/docker-compose.yml up -d

#wait for app to become up, alternatively use spring boot's actuator endpoint
export CONTEXT_PATH=$CONTEXT_PATH; bash -c 'while [[ "$(curl -k -s -w ''%{http_code}'' -o /dev/null http://localhost:8080$CONTEXT_PATH/recipes)" != "200" ]]; do echo "Waiting for app to become up"; sleep 5; done'

echo ">> Running test automation against $IMAGE backed by benchmarked datastore in Java 11"
# note: as of 15+ Karate is no longer support for lack of nashorn engine, so we set it to 11. Error otherwise: Caused by: java.lang.NullPointerException: Cannot invoke "javax.script.ScriptEngine.createBindings()" because "com.intuit.karate.ScriptBindings.NASHORN" is null
export JAVA_HOME=`/usr/libexec/java_home -v 11`
mvn test -pl testautomation -Dtest=KarateRunner -Dkarate.env=develop -Denv_route_url=http://localhost:8080$CONTEXT_PATH
#echo ">> Running test automation done. Setting JAVA_HOME back to Java 15"
#export JAVA_HOME=`/usr/libexec/java_home -v 15`
#
echo ">> Done running test automation against $IMAGE backed by benchmarked datastore"
echo "Shutdown with command:"
echo "docker-compose -f api/docker/release/docker-compose.yml rm -fsv"

echo ">> Test automation done. If passed, all OK."
