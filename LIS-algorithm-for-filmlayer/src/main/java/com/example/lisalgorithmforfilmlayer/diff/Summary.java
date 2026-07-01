package com.example.lisalgorithmforfilmlayer.diff;

/** 결과 요약. 이동 최소성(공통 − 앵커 = 이동 수)을 한눈에 보이려고 common/anchors도 담는다. */
public record Summary(int beforeSize, int afterSize,
                      int deletes, int inserts, int moves, int updates,
                      int common, int anchors) {
}
