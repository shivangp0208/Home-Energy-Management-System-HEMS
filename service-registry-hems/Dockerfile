# 1. Base Image: Java 17 Temurin on Alpine Linux
FROM eclipse-temurin:17-jre-alpine

# 2. Security: Create and use a non-root user
RUN addgroup -S hemsgroup && adduser -S hemsuser -G hemsgroup
USER hemsuser:hemsgroup

# 3. Workspace: Set the directory inside the container
WORKDIR /app

# 5. Application Artifact: Copy the JAR built by GitHub Actions/Gradle
COPY build/libs/*.jar app.jar

# 6. Ports: Use the EXPOSE instruction to document the port.

EXPOSE 8091

# 7. Execution: Command to run your Java application
ENTRYPOINT ["java", "-jar", "app.jar"]