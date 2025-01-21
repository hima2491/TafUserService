FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/my-userservice-app.jar app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]