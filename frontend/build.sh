#!/bin/bash
set -e
# Any subsequent(*) commands which fail will cause the shell script to exit immediately
DATE=$(date '+%Y-%m-%d')
GITVERSION=$(git rev-parse --short=8 HEAD)
VERSION="$DATE-build-$GITVERSION"
echo $VERSION
docker build -t degeus.io/recipe-app-spa:$VERSION .
docker tag degeus.io/recipe-app-spa:$VERSION degeus.io/recipe-app-spa:latest
