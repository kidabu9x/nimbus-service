envsubst < config.$ENVIRONMENT.yml > k8s-config.yml && kubectl apply -f k8s-config.yml -n dev

envsubst < dev.main.yml > k8s-main.yml && kubectl apply -f k8s-main.yml -n dev

#envsubst < migration-job.yml > k8s-migration.yml && kubectl apply -f k8s-migration.yml
