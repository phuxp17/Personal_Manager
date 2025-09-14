FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar personalmanager-v1.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "personalmanager-v1.jar"]