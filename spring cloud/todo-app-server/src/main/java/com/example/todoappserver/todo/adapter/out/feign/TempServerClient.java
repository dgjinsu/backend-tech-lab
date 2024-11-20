package com.example.todoappserver.todo.adapter.out.feign;

import com.example.todoappserver.todo.adapter.in.web.dto.TodoResponse;
import com.example.todoappserver.common.dto.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "temp-server")
public interface TempServerClient {

    @PostMapping("/temp-server/api/v1/temps")
    ResponseEntity<Response> saveTemp(@RequestBody TodoResponse response);
}