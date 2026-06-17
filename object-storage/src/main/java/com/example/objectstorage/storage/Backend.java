package com.example.objectstorage.storage;

/**
 * 비교 대상 오브젝트 스토리지 백엔드.
 * application.yml 의 storage.backends.&lt;key&gt; 와 1:1로 매핑된다(key = 이름 소문자).
 */
public enum Backend {
    SEAWEEDFS("SeaweedFS"),
    GARAGE("Garage");

    private final String label;

    Backend(String label) {
        this.label = label;
    }

    /** 화면 표시용 이름 (예: "SeaweedFS"). */
    public String label() {
        return label;
    }

    /** 설정/경로용 키 (예: "seaweedfs"). */
    public String key() {
        return name().toLowerCase();
    }

    /** 경로 변수 등 문자열로부터 백엔드를 해석한다. 잘못된 값이면 IllegalArgumentException. */
    public static Backend from(String s) {
        return Backend.valueOf(s.trim().toUpperCase());
    }
}
