package com.coursework.web.controllers;

import com.coursework.domain.dto.UserDto;
import com.coursework.persistence.services.UserService;
import com.coursework.web.security.AuthenticationUser;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class RegistrationController {

    private static final int WEAK_PASS = 4;
    private static final int MID_PASS = 8;

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(@AuthenticationPrincipal AuthenticationUser authenticationUser, Model model) {

        if (authenticationUser != null) {
            return "redirect:/";
        }

        model.addAttribute("user", new UserDto());
        return "/registrationForm";
    }

    @PostMapping("/registration")
    public String registerNewUser(@Valid @ModelAttribute("user") UserDto userDto,
                                  BindingResult bindingResult, Model model) {

        userService.userValidation(userDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "/registrationForm";
        }

        userService.save(userDto);

        model.addAttribute("user", userDto);
        return "/success";
    }

    @ResponseBody
    @GetMapping(value = "/check-strength", produces = {"text/html; charset=UTF-8"})
    public String checkStrength(@RequestParam String password) {

        if (password.length() == 0 || password.isBlank()) {
            return "";
        }

        if (password.length() <= WEAK_PASS) {
            return "weak";
        } else if (password.length() <= MID_PASS) {
            return "medium";
        } else {
            return "strong";
        }

    }
}
