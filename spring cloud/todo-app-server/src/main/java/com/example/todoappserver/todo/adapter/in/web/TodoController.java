package com.example.todoappserver.todo.adapter.in.web;

import com.example.common.annotation.MemberRole;
import com.example.common.security.PrincipalDetails;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoListResponse;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoResponse;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoSaveRequest;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoUpdateRequest;
import com.example.todoappserver.todo.application.port.in.SaveTodoUseCase;
import com.example.todoappserver.todo.application.port.in.DeleteTodoUseCase;
import com.example.todoappserver.todo.application.port.in.GetTodoQuery;
import com.example.todoappserver.todo.application.port.in.UpdateTodoUseCase;
import com.example.todoappserver.common.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/todo-app/api/v1/todos")
@RequiredArgsConstructor
@Slf4j
public class TodoController {

    private final SaveTodoUseCase saveTodoUseCase;
    private final DeleteTodoUseCase deleteTodoUseCase;
    private final GetTodoQuery getTodoQuery;
    private final UpdateTodoUseCase updateTodoUseCase;

    @Operation(summary = "TODO 저장")
    @PostMapping("")
    public ResponseEntity<Response<TodoResponse>> saveTodo(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody TodoSaveRequest request) {
        return ResponseEntity.ok(
            new Response<TodoResponse>(
                saveTodoUseCase.saveTodo(principalDetails.getMember().getId(), request),
                "TODO 저장 완료"));
    }

    //    @RequireRole("ROLE_MEMBER")
//    @AdminRole
    @MemberRole
    @Operation(summary = "TODO 리스트 조회")
    @GetMapping("")
    public ResponseEntity<Response<TodoListResponse>> getTodoList(
        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(
            new Response<TodoListResponse>(getTodoQuery.getTodoList(principalDetails.getMember()
                .getId()), "TODO 리스트 조회 완료"));
    }

    @Operation(summary = "TODO 수정")
    @PatchMapping("/{todoId}")
    public ResponseEntity<Response<TodoResponse>> updateTodo(@PathVariable Long todoId,
        @RequestBody TodoUpdateRequest request) {
        return ResponseEntity.ok(
            new Response<TodoResponse>(updateTodoUseCase.updateTodo(todoId, request),
                "TODO 내용 수정 완료"));
    }

    @Operation(summary = "완료된 TODO 제거")
    @DeleteMapping("/completed")
    public ResponseEntity<Response<Void>> deleteCompletedTodo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        deleteTodoUseCase.deleteCompletedTodo(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response<Void>("완료된 TODO 제거 완료"));
    }

    @Operation(summary = "TODO 개별 제거")
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Response<Void>> deleteTodoById(@PathVariable Long todoId) {
        deleteTodoUseCase.deleteTodoById(todoId);
        return ResponseEntity.ok(new Response<Void>("TODO 개별 제거 완료"));
    }
}
