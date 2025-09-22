FROM openjdk:21-jdk-slim
WORKDIR /app
COPY applications/app-service/build/libs/*.jar app.jar
EXPOSE 8082
CMD ["java", "-jar", "app.jar"]