package test.springEvent.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlimTalkService {
    public void send(String name) throws Exception {
        Thread.sleep(3000); // 3초 sleep
        System.out.println(name + "에게 알림톡 발송");
        throw new Exception("에러 발생");
    }
}
