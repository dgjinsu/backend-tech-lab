package com.example.springfqcn.controller.b;

import com.example.springfqcn.service.b.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BController {

    private final UserService userService;

    @GetMapping("/test2")
    public String test2() {
        return userService.getName();
    }
}
