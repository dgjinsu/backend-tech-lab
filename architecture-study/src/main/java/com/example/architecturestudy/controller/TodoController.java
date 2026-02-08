package com.example.architecturestudy.controller;

import com.example.architecturestudy.dto.GetTodoRes;
import com.example.architecturestudy.dto.SaveTodoReq;
import com.example.architecturestudy.dto.SaveTodoRes;
import com.example.architecturestudy.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<SaveTodoRes> save(@RequestBody @Valid SaveTodoReq request) {
        return ResponseEntity.ok(todoService.save(request));
    }

    @GetMapping("/todos")
    public ResponseEntity<GetTodoRes> getTodos() {
        return ResponseEntity.ok(todoService.getTodos());
    }
}
