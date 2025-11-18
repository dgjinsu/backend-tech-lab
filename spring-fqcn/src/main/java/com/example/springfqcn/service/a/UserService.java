package com.example.springfqcn.service.a;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    public UserDto getName() {
        return "UserService from service package";
    }
}

