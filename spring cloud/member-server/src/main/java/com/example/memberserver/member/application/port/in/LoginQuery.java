package com.example.memberserver.member.application.port.in;

import com.example.memberserver.member.adaptor.in.web.dto.LoginRequest;
import com.example.memberserver.member.adaptor.in.web.dto.LoginResponse;

public interface LoginQuery {
    LoginResponse login(LoginRequest request);
}
