package test.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockService {
    private final StockRepository stockRepository;
    private final RedisLockRepository redisLockRepository;

    public void decrease(Long stockId, Long val) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(stockId);
        stock.decrease(val);
    }
}
