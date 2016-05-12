#!/bin/bash

set -e -x

pushd source-code
  ./mvnw clean package
popd

git clone ./source-code ./build-output
#cp source-code/target/account-service-0.0.1-SNAPSHOT.jar build-output/.