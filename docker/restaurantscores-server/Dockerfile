# https://g00glen00b.be/docker-spring-boot/
# Using the alpine image as a base​
FROM openjdk:8-jdk-alpine

# maintainer
MAINTAINER "Jane Ullah <janeullah@gmail.com>"

# http://blog.zot24.com/tips-tricks-with-alpine-docker/ For keeping size small
RUN apk add --no-cache curl

# https://stackoverflow.com/questions/17466699/not-able-to-build-a-specific-dockerfile
# https://www.ctl.io/developers/blog/post/dockerfile-add-vs-copy/
# Copy the current directory contents into the container at /app
# Use for gradle docker building since a task copies the jar
# COPY target/RestaurantScores*.jar app.jar

# Use for manual docker building i.e. docker build
COPY build/libs/RestaurantScores*.jar app.jar

ARG java_opts=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000
ARG server_port=8080
ARG catalina_home=/usr/local/tomcat
ARG spring_active_profile=postgresql

# Setting environment variables
# Note: this application relies on environment variables that contain secrets. Can pass along to image via:
# https://stackoverflow.com/questions/30494050/how-do-i-pass-environment-variables-to-docker-containers
ENV JAVA_OPTS $java_opts

ENV PORT $server_port

# Setting catalina home
ENV CATALINA_HOME $catalina_home

# Setting spring active profile
ENV SPRING_PROFILES_ACTIVE $spring_active_profile

# https://blog.codeship.com/a-beginners-guide-to-the-dockerfile/
# Expose debug port and port 8080
EXPOSE 8000 8080

# https://spring.io/guides/gs/spring-boot-docker/
VOLUME /tmp

# https://stackoverflow.com/questions/41935435/understanding-volume-instruction-in-dockerfile
# Setup logging volume
VOLUME $CATALINA_HOME/logs

# Setup volume for file downloads
VOLUME $CATALINA_HOME/downloads/webpages

# Actuator health check
HEALTHCHECK --interval=15m --timeout=10s --retries=3 --start-period=1m CMD curl --fail http://localhost:8080/restaurantscores/health || exit 1

# http://containertutorials.com/docker-compose/spring-boot-app.html
# App entry point that must be used
ENTRYPOINT ["java"]

# Using cmd since it can be overridden at run time and Heroku deployment requires this
# https://www.ctl.io/developers/blog/post/dockerfile-entrypoint-vs-cmd/
CMD ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]