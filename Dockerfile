FROM maven:3.9.8-amazoncorretto-17-al2023 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/*.jar /app/task-manager.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/task-manager.jar"]