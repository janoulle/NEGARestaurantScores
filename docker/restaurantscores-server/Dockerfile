# https://g00glen00b.be/docker-spring-boot/
# Using the alpine image as a base​
FROM openjdk:8-jdk-alpine

# maintainer
MAINTAINER "Jane Ullah <janeullah@gmail.com>"

# http://blog.zot24.com/tips-tricks-with-alpine-docker/ For keeping size small
RUN apk add --no-cache curl
RUN apk add --update --no-cache openssh
RUN apk add --no-cache bash
RUN apk add --update --no-cache python

# Must pass the jar file location to docker at run time via --build-arg JAR_FILE=path/to/jarfile.jar
ARG JAR_FILE

# Must pass in the port
ARG PORT

ARG HEROKU_EXEC_FILE

# https://stackoverflow.com/questions/17466699/not-able-to-build-a-specific-dockerfile
# https://www.ctl.io/developers/blog/post/dockerfile-add-vs-copy/
# Copy the current directory contents into the container at /app
COPY ${JAR_FILE} app.jar

# Not working atm. Will revisit later
# https://devcenter.heroku.com/articles/exec#enabling-docker-support
# COPY ${HEROKU_EXEC_FILE} /app/.profile.d
# ADD ./.profile.d /app/.profile.d
# RUN rm /bin/sh && ln -s /bin/bash /bin/sh

# Setting environment variables
# Note: this application relies on environment variables that contain secrets. Can pass along to image via:
# https://stackoverflow.com/questions/30494050/how-do-i-pass-environment-variables-to-docker-containers
ENV JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000

# Setting catalina home
ENV CATALINA_HOME /usr/local/tomcat

# Setting spring active profile
# Comment out for each running container to set this field
# ENV SPRING_PROFILES_ACTIVE postgresql

# https://blog.codeship.com/a-beginners-guide-to-the-dockerfile/
# Expose debug port and port 8080
# Not honored by Heroku
EXPOSE 8000 8080

# https://spring.io/guides/gs/spring-boot-docker/
# https://stackoverflow.com/questions/41935435/understanding-volume-instruction-in-dockerfile
# Setup logging volume and file download paths. If not mapped to host volume, it will save inside the container
VOLUME [ "$CATALINA_HOME/logs", "$CATALINA_HOME/downloads/webpages"]

# https://docs.docker.com/engine/reference/builder/#healthcheck
# Spring actuator health check
HEALTHCHECK --interval=15m --timeout=10s --retries=3 --start-period=1m CMD curl --fail http://localhost:8080/restaurantscores/actuator/health || exit 1

# http://containertutorials.com/docker-compose/spring-boot-app.html
# App entry point that must be used
# ENTRYPOINT ["java"]

# Using cmd since it can be overridden at run time and Heroku deployment requires this
# https://www.ctl.io/developers/blog/post/dockerfile-entrypoint-vs-cmd/
# https://docs.docker.com/engine/reference/builder/#cmd
# If you want shell processing then either use the shell form or execute a shell directly, for example: [ "sh", "-c", "echo $HOME" ]
# https://stackoverflow.com/questions/23071214/use-environment-variables-in-cmd
CMD exec java $JAVA_OPTS -Dserver.port=$PORT -Djava.security.egd=file:/dev/./urandom -jar /app.jar

# Note: Leaving off server.port causes heroku deployment error
# CMD ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]