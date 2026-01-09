package com.example.nexusnotificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationCreateRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
