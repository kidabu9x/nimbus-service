package vn.com.nimbus.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/v1")
public class AuthController {
    @PostMapping("/login")
    public String login() {
        return "Hello World!!!";
    }
}
