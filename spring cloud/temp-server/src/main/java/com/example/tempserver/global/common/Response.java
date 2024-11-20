package com.example.tempserver.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class Response<T> {

    private T data;
    private String message;

    public Response(String message) {
        this.message = message;
    }
}