FROM eclipse-temurin:21-jdk-jammy

LABEL solution=pwm
LABEL category=service

COPY ./*.jar app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]