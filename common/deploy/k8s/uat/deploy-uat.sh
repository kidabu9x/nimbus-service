#!/usr/bin/env bash
kubectl delete job $JOB_NAME --ignore-not-found=true -n uat

envsubst < config.$ENVIRONMENT.yml > k8s-config.yml && kubectl apply -f k8s-config.yml -n uat

envsubst < migration-job.yml > k8s-migration.yml && kubectl apply -f k8s-migration.yml -n uat

