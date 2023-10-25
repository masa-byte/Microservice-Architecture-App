FROM openjdk:17-jdk-slim-buster

# Copy the JAR file into the container
COPY target/CustomerMicroservice-0.0.1-SNAPSHOT.jar /app/CustomerMicroservice.jar

# Set the working directory
WORKDIR /app

# Specify the command to run your microservice
CMD ["java", "-jar", "CustomerMicroservice.jar"]