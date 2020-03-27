FROM mysql:8.0.18

ENV MYSQL_ROOT_PASSWORD=""
ENV MYSQL_ALLOW_EMPTY_PASSWORD="yes"

COPY mysqld.cnf /etc/mysql/mysql.conf.d/mysqld.cnf
ADD db-setup.sql /docker-entrypoint-initdb.d

EXPOSE 3306