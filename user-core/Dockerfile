FROM amazoncorretto:17
ARG JAR_FILE=target/user-core-1.0.0.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]