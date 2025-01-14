package com.example.springbootrabbitmq.member.service;

import com.example.springbootrabbitmq.infrastructure.rabbitmq.MemberProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberProducer memberProducer;

    public void sendRequest(String name) {
        memberProducer.sendMemberName(name);
    }

}
