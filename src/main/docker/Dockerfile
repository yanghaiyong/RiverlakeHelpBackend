FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE ${SERVER_PORT:-8080}

ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -jar app.jar"]
