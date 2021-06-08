package com.orange.credicard.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class WelcomeController {

    @GetMapping
    public String arrived() {
        return "Welcome! Everything is fine.";
    }
}
