package com.example.parallelprocessing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

@Slf4j
@Service
public class FileStorageService {

    private final ObjectMapper objectMapper;
    private final Path basePath;

    public FileStorageService(@Value("${poc.storage.path:/storage/poc}") String storagePath) {
        this.basePath = Paths.get(storagePath);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveJsonFile(int fileId, Map<String, Object> data) throws IOException {
        String batchDir = String.format("batch_%03d", (fileId - 1) / 100 + 1);
        String fileName = String.format("data_%05d.json", fileId);

        Path dirPath = basePath.resolve(batchDir);
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve(fileName);
        byte[] jsonBytes = objectMapper.writeValueAsBytes(data);
        Files.write(filePath, jsonBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void cleanup() throws IOException {
        if (!Files.exists(basePath)) {
            return;
        }
        Files.walkFileTree(basePath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        log.info("Cleanup completed: {}", basePath);
    }

    public Path getBasePath() {
        return basePath;
    }
}
