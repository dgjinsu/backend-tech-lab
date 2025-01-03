package com.example.cleanarchitecture.core.dto.query;

import java.util.List;

public record MemberListQuery(
    List<MemberQuery> memberQueryList
) {

    public record MemberQuery(
        Long memberId,
        String loginId,
        String password
    ) {

    }
}
