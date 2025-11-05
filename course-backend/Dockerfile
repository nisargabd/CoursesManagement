FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . .
RUN apt-get update && apt-get install -y maven
RUN mvn clean install -DskipTests
RUN mvn clean package -DskipTests
COPY target/*.jar app.jar
EXPOSE 9099
ENTRYPOINT ["java", "-jar", "app.jar"]
