#!/usr/bin/env bash

envsubst < config.$ENVIRONMENT.yml > k8s-config.yml && kubectl apply -f k8s-config.yml -n uat

envsubst < $ENVIRONMENT.main.yml > k8s-main.yml && kubectl apply -f k8s-main.yml -n uat

#envsubst < migration-job.yml > k8s-migration.yml && kubectl apply -f k8s-migration.yml

