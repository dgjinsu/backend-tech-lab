# Java 21 OpenJDK 이미지를 기반으로 설정
FROM eclipse-temurin:17-jdk-jammy

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY build/libs/java17-scouter-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# 애플리케이션 실행 명령
ENTRYPOINT [ "java", "-jar", "-javaagent:/agent.java/scouter.agent.jar", "-Dscouter.config=/agent.java/conf/scouter.conf", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "/app/app.jar" ]