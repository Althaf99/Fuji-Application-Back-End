# Fetching latest version of Java
FROM openjdk:18

# Setting up work directory
WORKDIR /app

# Copy the jar file into our app
COPY ./target/fujicraft_application-0.0.1-SNAPSHOT.jar /app

# Copy the application.properties file
COPY src/main/resources/application.properties /app/

# Exposing port 443
EXPOSE 443

# Starting the application
CMD ["java", "-jar", "fujicraft_application-0.0.1-SNAPSHOT.jar"]