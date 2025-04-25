# base image with Java 17 runtime
FROM eclipse-temurin:17-jre-alpine

# install Python3 and pip (only if not already in the base image)
RUN apk update && apk add --no-cache python3 py3-pip

# working directory
WORKDIR /app

# copy the JAR file
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# expose the port that the backend will run on
EXPOSE 8080

# set the default command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
