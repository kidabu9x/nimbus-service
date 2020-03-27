FROM mysql:5.7

ENV MYSQL_ROOT_PASSWORD=""
ENV MYSQL_ALLOW_EMPTY_PASSWORD="yes"

ADD db-setup.sql /docker-entrypoint-initdb.d

EXPOSE 3306