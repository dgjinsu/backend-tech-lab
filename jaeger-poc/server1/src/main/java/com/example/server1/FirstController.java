package com.example.server1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FirstController {

    private final MemberRepository memberRepository;

    @PostMapping("/save")
    public String save() {
        memberRepository.save(new Member());
        log.info("[server1 save]");
        return "server1 save";
    }

    @GetMapping("/get")
    public String get() {
        String url = "http://localhost:8081/receive";

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        log.info("[server1 get] 요청 결과: {}", response);

        return response;
    }


}
