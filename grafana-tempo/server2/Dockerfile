FROM eclipse-temurin:21-jdk AS build

# JAR 파일 복사 (빌드된 파일)
COPY target/*.jar app.jar

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

# 컨테이너 실행 시 JAR 파일 실행
#ENTRYPOINT ["java", "-jar", "/app.jar"]
ENTRYPOINT java -javaagent:opentelemetry-javaagent.jar \
           -Dotel.traces.exporter=logging \
           -Dotel.metrics.exporter=logging \
           -Dotel.logs.exporter=logging \
           -jar /app.jar
