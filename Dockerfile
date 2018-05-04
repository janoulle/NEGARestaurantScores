# https://g00glen00b.be/docker-spring-boot/
# Using the alpine image as a base​
FROM openjdk:8-jdk-alpine

# maintainer
MAINTAINER "Jane Ullah <janeullah@gmail.com>"

# http://blog.zot24.com/tips-tricks-with-alpine-docker/ For keeping size small
RUN apk add --no-cache curl

# Copy the current directory contents into the container at /app
ADD target/RestaurantScores*.jar app.jar

# Actuator health check
HEALTHCHECK --interval=15m --timeout=10s --retries=3 --start-period=1m CMD curl --fail http://localhost:8080/restaurantscores/health || exit 1

# http://containertutorials.com/docker-compose/spring-boot-app.html
# App entry point
ENTRYPOINT exec java -Djava.security.egd=file:/dev/./urandom -jar /app.jar