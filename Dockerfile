FROM openjdk:11-jdk as build
ARG version='0.0.1-SNAPSHOT'
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY build.gradle gradlew settings.gradle $APP_HOME
COPY gradle gradle

RUN sh ./gradlew build -x bootJar -x test --continue

COPY src src

RUN sh ./gradlew build -x checkstyleMain -x checkstyleTest -x test

FROM openjdk:11.0
VOLUME /tmp
COPY src /usr/app/src
COPY --from=build /usr/app/build/libs/*.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "/app.jar"]