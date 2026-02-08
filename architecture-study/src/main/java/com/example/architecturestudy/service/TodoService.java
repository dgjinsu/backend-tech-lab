package com.example.architecturestudy.service;

import com.example.architecturestudy.dto.GetTodoRes;
import com.example.architecturestudy.dto.GetTodosRes;
import com.example.architecturestudy.dto.SaveTodoReq;
import com.example.architecturestudy.dto.SaveTodoRes;
import com.example.architecturestudy.dto.UpdateTodoReq;
import com.example.architecturestudy.dto.UpdateTodoRes;
import com.example.architecturestudy.dto.UpdateTodoStatusReq;
import com.example.architecturestudy.dto.UpdateTodoStatusRes;
import com.example.architecturestudy.entity.TodoEntity;
import com.example.architecturestudy.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public SaveTodoRes save(SaveTodoReq req) {
        TodoEntity todo = todoRepository.save(req.toEntity());
        return SaveTodoRes.from(todo);
    }

    public GetTodosRes getTodos() {
        List<GetTodosRes.Todo> todos = todoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(GetTodosRes.Todo::from)
                .toList();
        return new GetTodosRes(todos);
    }

    public GetTodoRes getTodo(Long id) {
        TodoEntity todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("엔티티를 발견할 수 없습니다."));
        return GetTodoRes.from(todo);
    }

    @Transactional
    public UpdateTodoRes updateTodo(Long id, UpdateTodoReq req) {
        TodoEntity todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("엔티티를 발견할 수 없습니다."));
        todo.updateTitleAndContent(req.title(), req.content());
        return UpdateTodoRes.from(todo);
    }

    @Transactional
    public UpdateTodoStatusRes updateTodoStatus(Long id, UpdateTodoStatusReq req) {
        TodoEntity todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("엔티티를 발견할 수 없습니다."));
        todo.updateStatus(req.status());
        return UpdateTodoStatusRes.from(todo);
    }
}
