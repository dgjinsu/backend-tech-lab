package com.example.objectstorage.benchmark;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.objectstorage.storage.Backend;
import com.example.objectstorage.storage.S3StorageService;

/**
 * 동일한 작업(N개 업로드 → N개 다운로드 → 일괄 삭제)을 백엔드에 돌려
 * 지연(총/평균/p50/p95)과 처리량(MB/s)을 측정한다.
 *
 * 순차(single-thread) 측정이라 절대 성능보다는 "같은 코드/같은 작업에서의 상대 비교"에 의미가 있다.
 */
@Service
public class BenchmarkService {

    private static final Logger log = LoggerFactory.getLogger(BenchmarkService.class);
    private static final String PREFIX = "__bench__/";

    private final S3StorageService storage;

    public BenchmarkService(S3StorageService storage) {
        this.storage = storage;
    }

    public BenchmarkResult run(Backend b, int count, int sizeBytes) {
        log.info("[bench:{}] 시작 count={} size={}B", b.label(), count, sizeBytes);

        byte[] payload = new byte[sizeBytes];
        ThreadLocalRandom.current().nextBytes(payload);

        // 이전 잔여물 정리
        storage.deleteAll(b, PREFIX);

        // 업로드
        long[] up = new long[count];
        long upStart = System.nanoTime();
        for (int i = 0; i < count; i++) {
            long t = System.nanoTime();
            storage.upload(b, PREFIX + i, payload, "application/octet-stream");
            up[i] = System.nanoTime() - t;
        }
        long upTotal = System.nanoTime() - upStart;

        // 다운로드
        long[] down = new long[count];
        long downStart = System.nanoTime();
        for (int i = 0; i < count; i++) {
            long t = System.nanoTime();
            storage.download(b, PREFIX + i);
            down[i] = System.nanoTime() - t;
        }
        long downTotal = System.nanoTime() - downStart;

        // 정리(일괄 삭제)
        long delStart = System.nanoTime();
        storage.deleteAll(b, PREFIX);
        long delTotal = System.nanoTime() - delStart;

        double totalMB = (double) count * sizeBytes / (1024 * 1024);

        BenchmarkResult result = new BenchmarkResult(
                b.key(), b.label(), count, sizeBytes,
                ms(upTotal), avgMs(up), pctMs(up, 50), pctMs(up, 95), mbps(totalMB, upTotal),
                ms(downTotal), avgMs(down), pctMs(down, 50), pctMs(down, 95), mbps(totalMB, downTotal),
                ms(delTotal));

        log.info("[bench:{}] 완료 upload={}ms download={}ms", b.label(), result.uploadTotalMs(), result.downloadTotalMs());
        return result;
    }

    // --- 단위 변환 헬퍼 ---

    private static double ms(long nanos) {
        return round(nanos / 1_000_000.0);
    }

    private static double avgMs(long[] nanos) {
        long sum = 0;
        for (long n : nanos) {
            sum += n;
        }
        return nanos.length == 0 ? 0 : round((sum / (double) nanos.length) / 1_000_000.0);
    }

    /** 최근접 순위(nearest-rank) 백분위수. */
    private static double pctMs(long[] nanos, int pct) {
        if (nanos.length == 0) {
            return 0;
        }
        long[] sorted = nanos.clone();
        Arrays.sort(sorted);
        int idx = (int) Math.ceil(pct / 100.0 * sorted.length) - 1;
        idx = Math.max(0, Math.min(sorted.length - 1, idx));
        return round(sorted[idx] / 1_000_000.0);
    }

    private static double mbps(double totalMB, long totalNanos) {
        double seconds = totalNanos / 1_000_000_000.0;
        return seconds == 0 ? 0 : round(totalMB / seconds);
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
