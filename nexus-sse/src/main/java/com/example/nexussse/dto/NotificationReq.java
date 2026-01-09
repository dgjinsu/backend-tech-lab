package com.example.nexussse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReq {

    @NotBlank
    private String serviceId;

    @NotBlank
    private String userId;

    @NotBlank
    private String eventName;

    @NotNull
    private Object data;
}
