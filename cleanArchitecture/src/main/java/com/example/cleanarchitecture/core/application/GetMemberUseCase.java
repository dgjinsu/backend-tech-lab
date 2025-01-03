package com.example.cleanarchitecture.core.application;

import com.example.cleanarchitecture.core.dto.query.MemberListQuery;
import java.util.List;

public interface GetMemberUseCase {
    MemberListQuery getAllMembers();
}
