FROM eclipse-temurin:21-jdk AS build

# JAR 파일 복사 (빌드된 파일)
COPY build/libs/app.jar app.jar

ADD build/agent/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

# 컨테이너 실행 시 JAR 파일 실행
ENTRYPOINT java -javaagent:opentelemetry-javaagent.jar \
           -jar /app.jar

