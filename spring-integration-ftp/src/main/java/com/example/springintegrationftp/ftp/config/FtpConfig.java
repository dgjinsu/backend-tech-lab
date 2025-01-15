package com.example.springintegrationftp.ftp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizer;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore;

@Configuration
@IntegrationComponentScan
public class FtpConfig {

    @Bean
    public DefaultFtpSessionFactory ftpSessionFactory() {
        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
        factory.setHost("ftp.example.com");
        factory.setPort(21);
        factory.setUsername("ftpUser");
        factory.setPassword("ftpPassword");
        return factory;
    }

    @Bean
    public PropertiesPersistingMetadataStore metadataStore() {
        PropertiesPersistingMetadataStore store = new PropertiesPersistingMetadataStore();
        store.setBaseDirectory("metadata-store"); // 메타데이터를 저장할 로컬 디렉토리
        return store;
    }

    @Bean
    public FtpInboundFileSynchronizer ftpInboundFileSynchronizer(
        DefaultFtpSessionFactory ftpSessionFactory,
        PropertiesPersistingMetadataStore metadataStore) {
        return new CustomFtpInboundFileSynchronizer(ftpSessionFactory, metadataStore);
    }

    /**
     * 10분마다 FTP 작업 실행
     */
    @InboundChannelAdapter(channel = "ftpInputChannel", poller = @Poller(fixedRate = "600000"))
    public void pollFtpFiles(CustomFtpInboundFileSynchronizer synchronizer) {
        synchronizer.processRemoteFiles("/target/directory");
    }
}