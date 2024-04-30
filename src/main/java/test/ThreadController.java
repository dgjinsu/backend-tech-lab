package test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@Slf4j
public class ThreadController {
    static final AtomicLong atomic = new AtomicLong(0L);
    @GetMapping("/test")
    public String testEndpoint() throws InterruptedException {
        Thread.sleep(1000);  // 요청 처리에 1초가 걸린다고 가정
        long count = atomic.incrementAndGet();
        log.info("request processed = {}", count);
        return "요청-" + count + "\n";
    }
}
