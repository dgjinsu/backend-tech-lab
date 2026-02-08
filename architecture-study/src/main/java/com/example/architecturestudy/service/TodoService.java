package com.example.architecturestudy.service;

import com.example.architecturestudy.dto.GetTodoRes;
import com.example.architecturestudy.dto.SaveTodoReq;
import com.example.architecturestudy.dto.SaveTodoRes;
import com.example.architecturestudy.entity.TodoEntity;
import com.example.architecturestudy.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public SaveTodoRes save(SaveTodoReq request) {
        TodoEntity todo = todoRepository.save(request.toEntity());
        return SaveTodoRes.from(todo);
    }

    public GetTodoRes getTodos() {
        List<GetTodoRes.Todo> todos = todoRepository.findAll().stream()
            .map(GetTodoRes.Todo::from)
            .toList();
        return new GetTodoRes(todos);
    }
}
