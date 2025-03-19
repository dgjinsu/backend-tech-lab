package com.example.server1;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServerOneController {

    private final MemberRepository memberRepository;

    @GetMapping("/save")
    public String server1() {
      log.info("Server 1");
      memberRepository.save(Member.builder().name("name").build());
      return "Server 1";
    }

    @GetMapping("/get")
    public List<Member> server2() {
        return memberRepository.findAll();
    }
}
