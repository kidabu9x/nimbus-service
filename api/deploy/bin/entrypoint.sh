#!/bin/sh
#sed -e 's/}}/}/g' -e 's/{{/\${/g' config/$ENVIRONMENT-config.json > config/$ENVIRONMENT-config-tmp.json && mv config/$ENVIRONMENT-config-tmp.json config/$ENVIRONMENT-config.json
#envsubst < config/$ENVIRONMENT-config.json > config/$ENVIRONMENT-config-tmp.json && mv config/$ENVIRONMENT-config-tmp.json config/$ENVIRONMENT-config.json

java -Xms32m \
    -Xmx$JVM_XMX \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=Asia/Ho_Chi_Minh \
    -Duser.language=en \
    -Duser.country=US \
    -Djava.io.tmpdir=/tmp \
    -Denv=$ENVIRONMENT \
    -Dmode=DEPLOY \
    -jar app.jar

