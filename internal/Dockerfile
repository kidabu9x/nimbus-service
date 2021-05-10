FROM openjdk:8-jre-alpine

# Launch the verticle
WORKDIR /usr/work_home

RUN apk add --update \
    curl \
    openssl \
    tzdata \
    gettext \
    openssl \
    && rm -rf /var/cache/apk/*

# COPY target/classes/config/certs/ /usr/local/share/ca-certificates/
# RUN update-ca-certificates && \
#     ls -1 /usr/local/share/ca-certificates | while read cert; do \
#         openssl x509 -outform der -in /usr/local/share/ca-certificates/$cert -out $cert.der; \
#         keytool -import -alias $cert -keystore $JAVA_HOME/lib/security/cacerts -trustcacerts -file $cert.der -storepass changeit -noprompt; \
#         rm $cert.der; \
#     done

EXPOSE 8080

ENV JVM_XMX 256m

# Copy your fat jar to the container
COPY target/*-fat.jar app.jar

COPY target/classes/config/ config/

COPY logging logging

COPY deploy/bin/entrypoint.sh bin/entrypoint.sh
RUN chmod a+x bin/entrypoint.sh

ENTRYPOINT ["bin/entrypoint.sh"]
