package com.example.springintegrationftp.ftp.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.integration.ftp.filters.FtpPersistentAcceptOnceFileListFilter;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizer;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore;
import org.springframework.stereotype.Component;

@Component
public class CustomFtpInboundFileSynchronizer extends FtpInboundFileSynchronizer {

    private final DefaultFtpSessionFactory sessionFactory; // FTP 세션 팩토리

    public CustomFtpInboundFileSynchronizer(DefaultFtpSessionFactory sessionFactory,
        PropertiesPersistingMetadataStore metadataStore) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;

        // 새 파일만 동기화하기 위한 파일 필터 설정
        this.setFilter(new FtpPersistentAcceptOnceFileListFilter(metadataStore, "ftp-sync-"));
    }

    public void processRemoteFiles(String remoteDir) {
        List<FtpFileData> fileList = new ArrayList<>();
        exploreAndCollect(remoteDir, fileList);
        // todo: 파일 목록을 FtpProcessService로 전달하여 처리
    }

    private void exploreAndCollect(String remoteDir, List<FtpFileData> fileList) {
        try (var session = this.sessionFactory.getSession()) {
            FTPFile[] files = session.list(remoteDir); // 원격 디렉토리의 파일 and 디렉토리 목록

            for (FTPFile file : files) {
                if (file.isDirectory()) { // 디렉토리일 경우
                    String subDir = remoteDir + "/" + file.getName();
                    // 하위 디렉토리에 대해 재귀적으로 exploreAndCollect 호출
                    exploreAndCollect(subDir, fileList);
                } else { // 파일일 경우
                    try (InputStream inputStream = session.readRaw(
                        remoteDir + "/" + file.getName())) {
                        fileList.add(new FtpFileData(file.getName(), inputStream.readAllBytes()));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process directory: " + remoteDir, e);
        }
    }

    /**
     * 파일 데이터를 담는 내부 클래스
     */
    @AllArgsConstructor
    @Getter
    public static class FtpFileData {

        private final String fileName;
        private final byte[] content;
    }
}