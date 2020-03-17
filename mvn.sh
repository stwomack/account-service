#!/usr/bin/env bash
mvn --batch-mode release:prepare -DreleaseVersion=1.0.0 -DdevelopmentVersion=1.0.1-SNAPSHOT
