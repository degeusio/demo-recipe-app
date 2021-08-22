#!/bin/bash
set -e
# Any subsequent(*) commands which fail will cause the shell script to exit immediately
DATE=$(date '+%Y-%m-%d')
GITVERSION=$(git rev-parse --short=8 HEAD)
VERSION="$DATE-build-$GITVERSION"
echo $VERSION
docker build -t recipe-app:$VERSION .
docker tag recipe-app:$VERSION recipe:latest
