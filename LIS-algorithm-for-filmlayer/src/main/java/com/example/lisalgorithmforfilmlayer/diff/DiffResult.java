package com.example.lisalgorithmforfilmlayer.diff;

import java.util.List;

/**
 * diff 전체 결과 — 화면에 뿌릴 모든 것을 담는다.
 *
 * @param lisTable       §4.2 LIS 분석 표
 * @param executionPlan  §6 OLSA 실행 순서(삭제 desc → 삽입 asc → 수정)
 * @param verified       §5.1 메모리 사전검증 통과 여부 (apply(before,ops) == after)
 * @param verifyLog      사전검증 각 단계 스냅샷(줄바꿈 구분)
 */
public record DiffResult(
        List<Layer> before,
        List<Layer> after,
        List<LisRow> lisTable,
        List<DelRow> deletedRows,
        List<DiffOp> deletes,
        List<DiffOp> inserts,
        List<DiffOp> moves,
        List<DiffOp> updates,
        List<DiffOp> executionPlan,
        boolean verified,
        String verifyLog,
        Summary summary) {
}
