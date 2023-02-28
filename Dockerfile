FROM eclipse-temurin:19.0.2_7-jre-alpine

RUN addgroup -g 1000 java && adduser -Ss /bin/false -u 1000 -G java -h /home/java java

USER java
WORKDIR downloads

ENV TERM xterm-256color

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

ADD --chown=java:java build/libs/DownloadsAPI-*.jar /downloads/app.jar
