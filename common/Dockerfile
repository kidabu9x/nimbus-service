FROM webdevops/liquibase:mysql

COPY migration /liquibase

ENV MYSQL_DRIVER=com.mysql.cj.jdbc.Driver

COPY docker/bin/entrypoint.sh /liquibase/bin/entrypoint.sh
RUN chmod a+x /liquibase/bin/entrypoint.sh

ENTRYPOINT ["bin/entrypoint.sh"]