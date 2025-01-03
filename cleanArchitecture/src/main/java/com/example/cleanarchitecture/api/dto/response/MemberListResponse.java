package com.example.cleanarchitecture.api.dto.response;

import java.util.List;

public record MemberListResponse(
    List<MemberResponse> memberResponseList
) {

    public record MemberResponse(
        Long memberId,
        String loginId,
        String password
    ) {

    }
}
