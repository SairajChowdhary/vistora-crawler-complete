FROM maven:3.9.2-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
COPY config ./config
RUN mvn -B -DskipTests clean package
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /workspace/target/db-crawler-1.0.0.jar /app/app.jar
COPY --from=builder /workspace/config /app/config
EXPOSE 8080
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
