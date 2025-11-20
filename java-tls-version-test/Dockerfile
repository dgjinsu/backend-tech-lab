FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY build/libs/*.jar app.jar

# 포트 노출 (Spring Boot 기본 포트)
EXPOSE 8080

RUN sed -i 's|^jdk.tls.disabledAlgorithms=.*|jdk.tls.disabledAlgorithms=SSLv3, DTLSv1.0, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC, anon, NULL, ECDH|' /opt/java/openjdk/conf/security/java.security

# 애플리케이션 실행
ENTRYPOINT ["java", "-Djavax.net.debug=ssl,handshake", "-jar", "app.jar"]
#ENTRYPOINT ["java", "-jar", "app.jar"]

