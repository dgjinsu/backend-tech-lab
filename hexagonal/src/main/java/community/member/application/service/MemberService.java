package community.member.application.service;

import community.member.application.port.in.JoinUseCase;
import community.member.application.port.in.MemberListQuery;
import community.member.application.port.in.MemberUpdateUseCase;
import community.member.application.port.in.command.JoinCommand;
import community.member.application.port.in.command.MemberUpdateCommand;
import community.member.application.port.in.result.MemberResult;
import community.member.application.port.in.result.MemberUpdateResult;
import community.member.application.port.out.GetMemberListPort;
import community.member.application.port.out.GetMemberPort;
import community.member.application.port.out.SaveMemberPort;
import community.member.application.port.out.UpdateMemberPort;
import community.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberService implements JoinUseCase, MemberListQuery, MemberUpdateUseCase {

    private final SaveMemberPort saveMemberPort;
    private final GetMemberListPort getMemberListPort;
    private final UpdateMemberPort updateMemberPort;
    private final GetMemberPort getMemberPort;

    @Override
    public Long joinMember(JoinCommand command) {
        Member member = Member.builder()
            .loginId(command.getLoginId())
            .password(command.getPassword())
            .name(command.getName())
            .build();

        return saveMemberPort.saveMember(member);
    }

    @Override
    public List<MemberResult> getMemberList() {
        List<MemberResult> memberResultList = getMemberListPort.getMemberList();
        return memberResultList;
    }

    @Override
    public MemberUpdateResult updateMember(MemberUpdateCommand command) {
        Member member = getMemberPort.getMember(command.getMemberId());

        member.updateMember(command);

        updateMemberPort.updateMember(member);

        return MemberUpdateResult.from(member);
    }
}
