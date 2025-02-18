package com.example.server2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SecondController {

    @GetMapping("/receive")
    public String recieve() {
        log.info("[second receive");
        return "second server receive";
    }
}
