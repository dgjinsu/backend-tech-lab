FROM eclipse-temurin:21-jdk-jammy

LABEL solution=pwm
LABEL category=infra

COPY ./*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/app.jar"]