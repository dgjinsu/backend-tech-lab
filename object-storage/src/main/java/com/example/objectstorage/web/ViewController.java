package com.example.objectstorage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Thymeleaf 대시보드 진입점. */
@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
