FROM maven:3.8.1-jdk-11-slim AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B package

FROM openjdk:11-jre-slim
COPY wekafiles /root/wekafiles
COPY --from=builder /app/target/ApiWeka-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "/ApiWeka-0.0.1-SNAPSHOT.jar"]