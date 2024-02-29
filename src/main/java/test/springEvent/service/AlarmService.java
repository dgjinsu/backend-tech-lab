package test.springEvent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.springEvent.entity.Alarm;
import test.springEvent.entity.Member;
import test.springEvent.repository.AlarmRepository;
import test.springEvent.repository.MemberRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmService {
    private final AlarmRepository alarmRepository;
    public void send(String name) throws InterruptedException {
        alarmRepository.save(Alarm.builder().createdAt(LocalDateTime.now()).build());
        System.out.println(name + "에게 push 알림 발송");
        // 예외 발생
//        throw new RuntimeException("AlarmService 에러");
    }
}
