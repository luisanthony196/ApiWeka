FROM openjdk:11-jdk-slim
EXPOSE 8080
WORKDIR /root
ARG JAR_FILE=./target/ApiWeka-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} /root/app.jar
COPY "wekafiles" "/root/wekafiles"
ENTRYPOINT ["java", "-jar", "app.jar"]