package com.example.archunittest.member.application;

import com.example.archunittest.member.application.dto.MemberSaveCommand;
import com.example.archunittest.member.application.dto.MemberSaveQuery;

public interface MemberUseCase {

    MemberSaveQuery save(MemberSaveCommand command);

}
