kubectl delete job $JOB_NAME --ignore-not-found=true -n dev

envsubst < config.$ENVIRONMENT.yml > k8s-config.yml && kubectl apply -f k8s-config.yml -n dev

envsubst < migration-job.yml > k8s-migration.yml && kubectl apply -f k8s-migration.yml -n dev

