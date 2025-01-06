package com.example.securityjwt.global.security.util;

import com.example.securityjwt.global.security.dto.LoginMember;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static LoginMember getLoginMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            LoginMember loginMember = (LoginMember) authentication.getPrincipal();
            return loginMember;
        }
        return null;
    }
}
