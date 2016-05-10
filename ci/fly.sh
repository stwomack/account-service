#!/usr/bin/env bash
fly -t lite login -c http://192.168.100.4:8080
fly -t womack-ci set-pipeline --pipeline account-service --config pipeline.yml --load-vars-from cf-env.yml
fly -t womack-ci unpause-pipeline --pipeline account-service