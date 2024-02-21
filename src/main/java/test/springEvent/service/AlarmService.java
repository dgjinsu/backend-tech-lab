package test.springEvent.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlarmService {
    public void send(String name) throws InterruptedException {
        Thread.sleep(3000); // 3초 sleep
        System.out.println(name + "에게 push 알림 발송");
    }
}
