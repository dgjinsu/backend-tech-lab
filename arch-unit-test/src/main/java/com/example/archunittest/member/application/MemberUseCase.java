package com.example.archunittest.member.application;

import com.example.archunittest.member.application.dto.MemberSaveCommand;

public interface MemberUseCase {

    void save(MemberSaveCommand command);

}
