package com.example.javatlsversiontest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootApplication
public class JavaTlsVersionTestApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(JavaTlsVersionTestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 도커 컨테이너 내부의 MSSQL에 연결
        String ip = "mssql";  // 도커 서비스 이름
        int port = 1433;
        String username = "sa";
        String password = "rlawlstn1!";
        
        System.out.println("MSSQL 연결 테스트 시작...");
        
        // master 데이터베이스 연결 테스트 (항상 존재하는 기본 DB)
        String url = String.format("jdbc:sqlserver://%s:%d;databaseName=master;encrypt=true;trustServerCertificate=true",
                ip, port);
        
        System.out.println("\n연결 시도 중...");
        System.out.println("IP: " + ip);
        System.out.println("포트: " + port);
        System.out.println("데이터베이스: master");
        System.out.println("사용자명: " + username);
        System.out.println("연결 URL: " + url);
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("\n✓ MSSQL 연결 성공!");
            System.out.println("연결 테스트 완료!");
            
        } catch (Exception e) {
            System.err.println("\n✗ 연결 실패: " + e.getMessage());
            System.err.println("\n에러 상세:");
            e.printStackTrace();
        }
    }
}
