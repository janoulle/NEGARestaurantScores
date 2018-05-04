# https://g00glen00b.be/docker-spring-boot/
# Using the alpine image as a base​
FROM openjdk:8-jdk-alpine

# maintainer
MAINTAINER "Jane Ullah <janeullah@gmail.com>"

# http://blog.zot24.com/tips-tricks-with-alpine-docker/ For keeping size small
RUN apk add --no-cache curl

# Copy the current directory contents into the container at /app
ADD build/libs/RestaurantScores-0.0.1-SNAPSHOT.jar app.jar

# Setting environmental variables
ENV JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000

# Actuator health check
HEALTHCHECK --interval=15m --timeout=10s --retries=3 --start-period=1m CMD curl --fail http://localhost:8080/restaurantscores/health || exit 1

# http://containertutorials.com/docker-compose/spring-boot-app.html
# App entry point
ENTRYPOINT [ "sh", "-c", "java", "$JAVA_OPTS", "-Dspring.profiles.active=postgresql", "-Dserver.port=8080", "-Dserver.contextPath=/restaurantscores", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar" ]