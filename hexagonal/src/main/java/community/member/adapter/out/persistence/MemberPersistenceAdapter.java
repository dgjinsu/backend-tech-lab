package community.member.adapter.out.persistence;

import community.member.adapter.out.entity.MemberEntity;
import community.member.application.port.in.result.MemberResult;
import community.member.application.port.out.GetMemberListPort;
import community.member.application.port.out.GetMemberPort;
import community.member.application.port.out.SaveMemberPort;
import community.member.application.port.out.UpdateMemberPort;
import community.member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements SaveMemberPort, GetMemberListPort, UpdateMemberPort, GetMemberPort {

    private final MemberRepository memberRepository;

    @Override
    public Long saveMember(Member member) {
        MemberEntity memberEntity = MemberEntity.createEntity(member);
        return memberRepository.save(memberEntity).getId();
    }

    @Override
    public List<MemberResult> getMemberList() {
        List<MemberEntity> memberEntityList = memberRepository.findAll();
        List<MemberResult> memberResultList = memberEntityList.stream()
            .map(MemberResult::from)
            .collect(Collectors.toList());
        return memberResultList;
    }

    @Override
    public void updateMember(Member member) {
        System.out.println("updateMember");
        MemberEntity memberEntity = memberRepository.findById(member.getMemberId())
            .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        memberEntity.updateMember(member);
    }

    @Override
    public Member getMember(Long memberId) {
        System.out.println("selectMember");
        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));
        return Member.builder()
            .memberId(memberEntity.getId())
            .loginId(memberEntity.getLoginId())
            .name(memberEntity.getName())
            .password(memberEntity.getPassword())
            .build();
    }
}
