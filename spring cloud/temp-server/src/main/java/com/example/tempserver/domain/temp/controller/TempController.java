package com.example.tempserver.domain.temp.controller;

import com.example.tempserver.domain.temp.entity.Temp;
import com.example.tempserver.domain.temp.message.dto.TodoResponseDto;
import com.example.tempserver.domain.temp.message.dto.TodoSaveRequestDto;
import com.example.tempserver.domain.temp.service.TempService;
import com.example.tempserver.global.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/temp-server/api/v1/temps")
@RequiredArgsConstructor
@Slf4j
public class TempController {
    private final TempService tempService;

    @PostMapping("")
    public ResponseEntity<Response> saveTemp(@RequestBody TodoSaveRequestDto requestDto) {
        tempService.saveTemp(requestDto);
        return ResponseEntity.ok(new Response("Temp 저장 완료"));
    }
}
