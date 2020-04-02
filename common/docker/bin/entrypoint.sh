#!/usr/bin/env bash

liquibase  --classpath=/liquibase/lib/mysql-connector-java-8.0.13.jar --driver=$MYSQL_DRIVER --changeLogFile=/liquibase/changelog/master-changelog.xml --url=$JDBC_MASTER_URL --username=$JDBC_MASTER_USERNAME --password=$JDBC_MASTER_PASSWORD --contexts=manual,auto,$ENVIRONMENT update
