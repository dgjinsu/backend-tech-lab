package com.example.springfqcn.controller.a;

import com.example.springfqcn.service.a.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation
    .Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/test1")
    public UserDto test1() {
        return userService.getName();
    }



}

