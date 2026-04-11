# Use the lightweight Java 21 JRE base image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Explicitly copy the JAR from the ns-web module's target directory
COPY ns-web/target/*.jar app.jar

# CRITICAL: Restrict JVM memory to fit within the 1GB limit
ENV JAVA_OPTS="-Xmx1024m -Xms1024m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]