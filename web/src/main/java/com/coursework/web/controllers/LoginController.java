package com.coursework.web.controllers;

import com.coursework.web.security.AuthenticationUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Principal principal) {

        if (principal != null) {
            return "redirect:/";
        }
        return "/login";
    }

}
