package com.example.cleanarchitecture.core.application;

import com.example.cleanarchitecture.core.dto.command.SaveMemberCommand;
import com.example.cleanarchitecture.core.dto.query.MemberQuery;

public interface MemberSaveUseCase {
    MemberQuery saveMember(SaveMemberCommand command);
}
