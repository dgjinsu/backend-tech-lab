package com.example.springfqcn.service.b;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    public String getName() {
        return "UserService from service2 package";
    }
}

