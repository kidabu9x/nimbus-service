ARG MAVEN_VERSION=latest
FROM maven:${MAVEN_VERSION}

COPY docker/bootstrap.sh bin/bootstrap.sh
RUN chmod a+x bin/bootstrap.sh

ENTRYPOINT ["bin/bootstrap.sh"]