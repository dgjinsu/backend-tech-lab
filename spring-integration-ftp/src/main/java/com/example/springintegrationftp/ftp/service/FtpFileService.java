package com.example.springintegrationftp.ftp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FtpFileService {

    private final PollableChannel ftpChannel;

    @Scheduled(fixedRate = 600000) // 10분마다 실행
    public void processFiles() {
        System.out.println("Checking for new files...");

        Message<?> message;
        while ((message = ftpChannel.receive()) != null) {
            try (InputStream inputStream = (InputStream) message.getPayload();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                System.out.println("Processing file content:");
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // 파일 내용 출력 또는 처리
                }

            } catch (Exception e) {
                System.err.println("Error processing file: " + e.getMessage());
            }
        }
    }
}