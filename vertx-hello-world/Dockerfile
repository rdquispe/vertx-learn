# Docker example using fatjar
# - docker build -t example/vertx-hello-world .
# - docker run -t -i -p 8888:8888 example/vertx-hello-world

FROM adoptopenjdk:11-jre-hotspot

ENV FAT_JAR vertx-hello-world-1.0.0-SNAPSHOT-fat.jar
ENV APP_HOME /usr/app

EXPOSE 8888

COPY build/libs/$FAT_JAR $APP_HOME/

WORKDIR $APP_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $FAT_JAR"]
