# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:resolve
COPY . .
RUN mvn -q -DskipTests package

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/.env /app/.env
EXPOSE 9099
ENTRYPOINT ["java", "-jar", "app.jar"]
