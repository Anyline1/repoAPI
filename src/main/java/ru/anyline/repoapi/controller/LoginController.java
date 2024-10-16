package ru.anyline.repoapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginRedirect() {
        return "redirect:/repos";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

}
