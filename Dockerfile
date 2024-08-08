
ARG BUILDER="sbtscala/scala-sbt:eclipse-temurin-11.0.16_1.8.0_3.2.1"
ARG APPLICATION="eclipse-temurin:11-jre-focal"


FROM ${BUILDER} as build

ENV APP_PATH="/app"

WORKDIR ${APP_PATH}

COPY . .

RUN sbt -mem 8196 -J-Xms2G -J-Xss3M clean stage


FROM ${APPLICATION}

ENV APP_PATH="/app"

ENV TZ=Europe/Moscow \
    RESOURCE_FILE_PATH="$APP_PATH/resources/" \
    LOGGER_CONF_PATH=$APP_PATH/resources/logback.xml \
    CONFIG_FILE_PATH=$APP_PATH/resources/application.conf \
    CLASS_PATH="$APP_PATH/lib/*" \
    JAVA_OPTS="-Xms1024m -Xmx4G -Xss1M"

WORKDIR ${APP_PATH}

COPY --from=build /app/src/main/resources $APP_PATH/resources
COPY --from=build /app/target/universal/stage/lib $APP_PATH/lib

ENTRYPOINT java $JAVA_OPTS \
        -Dconfig.file=$CONFIG_FILE_PATH \
        -Dlogback.configurationFile=$LOGGER_CONF_PATH \
        -Dfile.encoding=UTF8 $@ \
        -cp "$CLASS_PATH" ru.chernyaev.testcrawlercore.Main $APP_PATH
