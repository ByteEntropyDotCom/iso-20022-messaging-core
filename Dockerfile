# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install curl for the healthcheck
RUN apk add --no-cache curl

# Security: Run as non-root
RUN addgroup -S payments && adduser -S processor -G payments
USER processor

COPY --from=build /app/target/*.jar app.jar

# Java 21 Optimizations: ZGC is perfect for low-latency payment rails
ENV JAVA_OPTS="-XX:+UseZGC -XX:+ZGenerational -Xms512m -Xmx512m"

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]