FROM openjdk:11-jdk as build
ARG version='0.0.1-SNAPSHOT'
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

RUN sh ./gradlew build -x checkstyleMain -x checkstyleTest

VOLUME /tmp
COPY --from=build /usr/app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]