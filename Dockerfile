# build stage with Maven
FROM maven:3.8-openjdk-17-slim AS build

# working directory for build
WORKDIR /app

# copy Maven files first for dependency caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# download dependencies (cached layer if pom.xml unchanged)
RUN mvn dependency:go-offline -B

# copy source code
COPY src ./src

# build the JAR file
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

# install Python3 and pip (only if not already in the base image)
RUN apk update && apk add --no-cache python3 py3-pip

# working directory
WORKDIR /app

# copy the JAR file from build stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# expose the port that the backend will run on
EXPOSE 8080

# set the default command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]