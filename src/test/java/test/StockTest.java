package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.stock.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class StockTest {
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private LettuceLockStockFacade lettuceLockStockFacade;
    @Autowired
    private RedissonLockStockFacade redissonLockStockFacade;

//    @BeforeEach
//    public void insert() {
//        List<Stock> stockList = new ArrayList<>();
//        for (long i = 0; i<100000; i++) {
//            stockList.add(new Stock(i, 100L));
//
//        }
//        stockRepository.saveAllAndFlush(stockList);
//    }
//
//    @AfterEach
//    public void delete() {
//        stockRepository.deleteAll();
//    }

    @Test
    public void decrease_test() {
        stockService.decrease(1L, 1L);

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - 1 = 99

        assertEquals(99, stock.getQuantity());
    }

    @Test
    public void 동시에_100명이_주문_비교() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latchRedis = new CountDownLatch(threadCount);
        CountDownLatch latchMySQL = new CountDownLatch(threadCount);

        // Redis 성능 테스트 시작 시간 기록
        long startTimeRedis = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    redissonLockStockFacade.decrease(1L, 1L); // redisson 사용
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latchRedis.countDown();
                }
            });
        }
        latchRedis.await();

        // Redis 성능 테스트 종료 시간 기록
        long endTimeRedis = System.currentTimeMillis();
        long durationRedis = endTimeRedis - startTimeRedis;
        System.out.println("Redis 수행 시간: " + durationRedis + " 밀리초");

        // MySQL 성능 테스트 시작 시간 기록
        long startTimeMySQL = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L); // mysql lock 사용
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latchMySQL.countDown();
                }
            });
        }
        latchMySQL.await();

        // MySQL 성능 테스트 종료 시간 기록
        long endTimeMySQL = System.currentTimeMillis();
        long durationMySQL = endTimeMySQL - startTimeMySQL;
        System.out.println("MySQL 수행 시간: " + durationMySQL + " 밀리초");

        // 최종 결과 확인
//        Stock stockRedis = stockRepository.findById(1L).orElseThrow();
//        assertEquals(0, stockRedis.getQuantity(), "Redis Lock: 재고 수량이 일치하지 않습니다.");


//        Stock stockMySQL = stockRepository.findById(1L).orElseThrow();
//        assertEquals(0, stockMySQL.getQuantity(), "MySQL Lock: 재고 수량이 일치하지 않습니다.");
    }
}
