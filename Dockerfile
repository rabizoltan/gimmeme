FROM openjdk:11-jdk as build
ARG version='0.0.1-SNAPSHOT'
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY build.gradle gradlew settings.gradle $APP_HOME
COPY gradle gradle

RUN sh ./gradlew build -x bootJar -x test --continue

COPY src src

RUN sh ./gradlew build -x checkstyleMain -x checkstyleTest

FROM openjdk:11.0
VOLUME /tmp

ENV APP_HOME=/usr/app/

COPY src /usr/app/src
COPY --from=build /usr/app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]