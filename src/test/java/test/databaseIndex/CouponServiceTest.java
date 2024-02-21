package test.databaseIndex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CouponServiceTest {
    @Autowired
    private CouponService couponService;

    @Test
    @DisplayName("동시에 쿠폰을 발급할 경우 - Lock 적용 x")
    void test_not_lock() throws InterruptedException {
        final int executeNumber = 20;

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 스레드의 실행이 끝날 때까지 대기
        CountDownLatch countDownLatch = new CountDownLatch(executeNumber);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < executeNumber; i++) {
            executorService.execute(() -> {
                try {
                    couponService.issueCoupon(1);
                    successCount.getAndIncrement();
                    System.out.println("쿠폰 발급");
                } catch (Exception e) {
                    failCount.getAndIncrement();
                    System.out.println(e.getMessage());
                }
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        System.out.println("발급된 쿠폰의 개수 = " + successCount.get());
        System.out.println("실패한 횟수 = " + failCount.get());

        assertEquals(failCount.get() + successCount.get(), executeNumber);
    }

}