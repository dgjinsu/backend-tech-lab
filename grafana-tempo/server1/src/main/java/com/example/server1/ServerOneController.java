package com.example.server1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ServerOneController {

    @GetMapping("/server1")
    public String server1() {
      log.info("Server 1");
      return "Server 1";
    }

}
