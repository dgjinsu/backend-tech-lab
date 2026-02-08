package com.example.architecturestudy.controller;

import com.example.architecturestudy.dto.GetTodoRes;
import com.example.architecturestudy.dto.GetTodosRes;
import com.example.architecturestudy.dto.SaveTodoReq;
import com.example.architecturestudy.dto.SaveTodoRes;
import com.example.architecturestudy.dto.UpdateTodoReq;
import com.example.architecturestudy.dto.UpdateTodoRes;
import com.example.architecturestudy.dto.UpdateTodoStatusReq;
import com.example.architecturestudy.dto.UpdateTodoStatusRes;
import com.example.architecturestudy.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<SaveTodoRes> save(@RequestBody @Valid SaveTodoReq req) {
        return ResponseEntity.ok(todoService.save(req));
    }

    @GetMapping("/todos")
    public ResponseEntity<GetTodosRes> getTodos() {
        return ResponseEntity.ok(todoService.getTodos());
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<GetTodoRes> getTodo(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodo(id));
    }

    @PatchMapping("/todos/{id}")
    public ResponseEntity<UpdateTodoRes> updateTodo(@PathVariable Long id, @RequestBody @Valid UpdateTodoReq req) {
        return ResponseEntity.ok(todoService.updateTodo(id, req));
    }

    @PatchMapping("/todos/{id}/status")
    public ResponseEntity<UpdateTodoStatusRes> updateTodoStatus(@PathVariable Long id, @RequestBody @Valid UpdateTodoStatusReq req) {
        return ResponseEntity.ok(todoService.updateTodoStatus(id, req));
    }
}
