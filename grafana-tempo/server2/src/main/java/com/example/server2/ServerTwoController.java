package com.example.server2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ServerTwoController {

    @GetMapping("/server2")
    public String server1() {
      log.info("Server 2");
      return "Server 2";
    }

}
