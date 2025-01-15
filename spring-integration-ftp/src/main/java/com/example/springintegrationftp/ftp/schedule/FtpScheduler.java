package com.example.springintegrationftp.ftp.schedule;

import com.example.springintegrationftp.ftp.config.CustomFtpInboundFileSynchronizer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FtpScheduler {

    private final CustomFtpInboundFileSynchronizer synchronizer;

    /**
     * 10분마다 실행
     */
    @Scheduled(fixedRate = 600000) // 10분 = 600,000ms
    public void scheduleFtpProcessing() {
        synchronizer.processRemoteFiles("/target/directory");
    }
}
