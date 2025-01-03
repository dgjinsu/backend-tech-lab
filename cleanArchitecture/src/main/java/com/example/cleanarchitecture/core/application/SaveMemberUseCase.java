package com.example.cleanarchitecture.core.application;

import com.example.cleanarchitecture.core.dto.command.SaveMemberCommand;
import com.example.cleanarchitecture.core.dto.query.MemberQuery;

public interface SaveMemberUseCase {
    MemberQuery saveMember(SaveMemberCommand command);
}
