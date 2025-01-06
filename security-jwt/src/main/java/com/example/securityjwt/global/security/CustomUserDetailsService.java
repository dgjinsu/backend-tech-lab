package com.example.securityjwt.global.security;

import com.example.securityjwt.domain.member.entity.Member;
import com.example.securityjwt.domain.member.repository.MemberRepository;
import com.example.securityjwt.global.security.dto.LoginMember;
import com.example.securityjwt.global.security.dto.MemberAuthInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.parseLong(memberId))
            .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

        return new LoginMember(
            new MemberAuthInfo(member.getId(), member.getRole())
        );
    }
}
