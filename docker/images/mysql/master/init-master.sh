#!/bin/bash

set -e

MYSQL_PWD=${MYSQL_ROOT_PASSWORD} mysql -u root -e "GRANT REPLICATION SLAVE ON *.* TO '$MYSQL_REPLICATION_USER'@'%' IDENTIFIED BY '$MYSQL_REPLICATION_PASSWORD';"
MYSQL_PWD=${MYSQL_ROOT_PASSWORD} mysql -u root -e 'flush privileges;'

echo "Grant user successfully"

