package com.example.notificationsse.controller;

import com.example.notificationsse.application.usecase.getallusers.GetAllUsers;
import com.example.notificationsse.application.usecase.getallusers.UserRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "회원", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final GetAllUsers getAllUsers;
    
    @Operation(summary = "전체 회원 조회", description = "모든 회원 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UserRes>> getUsers() {
        return ResponseEntity.ok(getAllUsers.getAllUsers());
    }
}

