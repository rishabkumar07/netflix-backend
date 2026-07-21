# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the pom first so dependency resolution gets its own cache layer —
# this layer only invalidates when pom.xml changes, not on every source edit
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
