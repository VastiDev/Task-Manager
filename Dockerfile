FROM openjdk:17
EXPOSE 8080
ADD target/task-manager.jar task-manager.jar
ENTRYPOINT ["java", "-jar", "/task-manager.jar"]