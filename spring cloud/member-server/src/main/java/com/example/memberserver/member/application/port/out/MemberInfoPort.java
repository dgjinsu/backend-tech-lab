package com.example.memberserver.member.application.port.out;

import com.example.memberserver.member.domain.Member;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor.MemberInfo;

public interface MemberInfoPort {
    MemberInfo getMemberInfo();
    Member findById(Long memberId);
    Member findByLoginId(String loginId);
}
