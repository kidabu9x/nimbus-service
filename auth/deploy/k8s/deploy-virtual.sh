#!/usr/bin/env bash
envsubst < virtualservice.yml > virtualservice-tmp.yml && mv virtualservice-tmp.yml virtualservice.yml && kubectl apply -f virtualservice.yml -n uat