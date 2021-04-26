FROM openjdk:11-jdk as build
ARG version='0.0.1-SNAPSHOT'
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY build.gradle gradlew settings.gradle $APP_HOME

RUN sh ./gradlew -PprojVersion=$version build -x checkstyleMain -x checkstyleTest

FROM openjdk:11.0-jre-slim-buster
VOLUME /tmp
COPY --from=build /usr/app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]