package com.example.lisalgorithmforfilmlayer.diff;

/** diff가 만들어 내는 4가지 연산 종류. */
public enum OpType {
    DELETE, // 삭제  — before에만 있음
    INSERT, // 생성  — after에만 있음
    MOVE,   // 이동  — 양쪽에 있으나 LIS 앵커에서 벗어남
    UPDATE  // 수정  — 양쪽에 있고 상대순서 유지, 내용만 다름
}
