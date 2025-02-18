# 1️⃣ OpenJDK 21 기반 이미지 사용
FROM eclipse-temurin:21-jdk AS build

# 2️⃣ OpenTelemetry Java Agent 다운로드
RUN curl -L -o /opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar

# 3️⃣ 작업 디렉터리 설정
WORKDIR /app

# 4️⃣ 애플리케이션 JAR 복사
COPY build/libs/server1-0.0.1-SNAPSHOT.jar /app/app.jar

# 5️⃣ OpenTelemetry 설정
ENV JAVA_TOOL_OPTIONS="-javaagent:/opentelemetry-javaagent.jar"

# 6️⃣ 애플리케이션 실행
CMD ["java", "-jar", "/app/app.jar"]
