package test.springEvent.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Async
    public void a() throws Exception {
        Thread.sleep(3000);
        throw new Exception("에러");
    }
}
