package com.example.tempserver.domain.temp.service;

import com.example.common.dto.TestDto;
import com.example.tempserver.domain.temp.entity.Temp;
import com.example.tempserver.domain.temp.message.dto.TodoSaveRequestDto;
import com.example.tempserver.domain.temp.repository.TempRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TempService {
    private final TempRepository tempRepository;

    public void saveTemp(TodoSaveRequestDto request) {
        Temp temp = Temp.builder().content(request.content()).createdAt(LocalDateTime.now()).todoId(request.todoId()).build();

        tempRepository.save(temp);

        TestDto test = new TestDto("test");
        log.info("test 데이터: {} ", test.getName());
//        throw new RuntimeException();
    }
}
