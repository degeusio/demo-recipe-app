#!/bin/bash
cd $(dirname $0)
# use ./build.sh --e PROD
while getopts e: flag
do
    case "${flag}" in
        e) env=${OPTARG};
    esac
done

git diff-index --quiet HEAD -- || echo "WARNING - YOU HAVE UNTRACKED CHANGES!";

echo "Selected env: $env"
echo "Selected messaging: $messaging"
if [ ! -z $env ] && [ $env == 'PROD' ]  #read if $env not is empty/null AND equals 'PROD'
then
  echo 'Selected env is PROD'
  PASSW_FILE="src/main/resources/passwords.prod.properties"
  LINES=$(wc -l $PASSW_FILE | awk '{ print $1 }') #print lines and with awk capture only number (first var)
  if [ $LINES -lt 5 ]
  then
    echo "Counted only [$LINES] lines in file [$PASSW_FILE]. Expected > 5, aborting. Did you enter the prod password contents from keepass?"
    exit 1
  fi
else
  echo 'Selected env is NOT PROD - you will make a new image for local testing'
fi
read -p "Press ENTER to continue"

echo ">> Building liquibase executable database jar."
mvn clean compile assembly:single

DATE=$(date '+%Y-%m-%d')
GITVERSION=$(git rev-parse --short=6 HEAD)
VERSION="$DATE-build-$GITVERSION"
IMAGE="degeus.io/recipe-app-db:$VERSION"
IMAGE_LATEST="degeus.io/recipe-app-db:latest"
JAR_WITH_DEPS="target/db-0.0.0-jar-with-dependencies.jar"
DB_NAME=recipedb
if [ $env == 'PROD' ]
then
  echo '>> PROD ommitted, skipping'
else
  echo "Creating new, empty postgres image that persists changes across db restarts"
  TEMP_IMAGE_NAME=recipe-app-db-local
  MASTER_JDBC_USER=postgres
  MASTER_JDBC_PASSWORD=mysecretpassword
  docker build -t $TEMP_IMAGE_NAME .
  DOCKER_CONTAINER_ID=$(docker run -d --rm -p5432:5432 -e POSTGRES_USER=$MASTER_JDBC_USER -e POSTGRES_PASSWORD=$MASTER_JDBC_PASSWORD -e POSTGRES_DB=$DB_NAME $TEMP_IMAGE_NAME)
  echo ">> Starting postgres in background with id [$DOCKER_CONTAINER_ID]"

  echo "pausing 5 seconds to allow for docker container [$DOCKER_CONTAINER_ID] to start up"
  sleep 5

  echo ">> Updating database schema against docker container $DOCKER_CONTAINER_ID"
  java --enable-preview -jar $JAR_WITH_DEPS

  echo ">> Committing changes applied to docker container $DOCKER_CONTAINER_ID"
  docker commit $DOCKER_CONTAINER_ID $IMAGE

  docker stop $DOCKER_CONTAINER_ID
  echo "docker image created: $IMAGE"
  echo "docker tag $IMAGE to $IMAGE_LATEST"
  docker tag $IMAGE $IMAGE_LATEST
  echo "run the image with:"
  echo "   docker run --rm -p5432:5432 $IMAGE"
fi
