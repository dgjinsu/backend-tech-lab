package com.example.server1;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Server1Application {

    public static void main(String[] args) {
        SpringApplication.run(Server1Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public OpenTelemetry openTelemetry() {
//        // Span Exporter 설정 (OTLP 사용)
//        SpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
//            .setEndpoint("http://localhost:4319") // OTLP gRPC 수집기
//            .setCompression("gzip")
//            .build();
//
//        // Tracer Provider 설정
//        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
//            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
//            .build();
//
//        // OpenTelemetry SDK 설정
//        return OpenTelemetrySdk.builder()
//            .setTracerProvider(tracerProvider)
//            .buildAndRegisterGlobal();
//    }
}
