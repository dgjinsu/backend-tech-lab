package com.example.server1;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServerOneController {

    private final MemberRepository memberRepository;
    private final RabbitMQProducer rabbitMQProducer;

    private final WebClient webClient = WebClient.builder().build();


    @GetMapping("/save")
    public String server1() {
      log.info("Server 1");
      memberRepository.save(Member.builder().name("name").build());
      return "Server 1";
    }

    @GetMapping("/get")
    public List<Member> server2() {
        String response = webClient.get()
            .uri("http://server2:8081/server2")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        log.info("Response from server2: {}", response);

        return memberRepository.findAll();
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam String message) {
        log.info("Sending message: {}", message);
        rabbitMQProducer.sendMessage(message);
        return "Message sent: " + message;
    }
}
