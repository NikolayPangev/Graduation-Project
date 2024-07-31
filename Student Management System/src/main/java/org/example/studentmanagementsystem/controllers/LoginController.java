package org.example.studentmanagementsystem.controllers;

import jakarta.validation.Valid;
import org.example.studentmanagementsystem.config.UserSession;
import org.example.studentmanagementsystem.model.dtos.LoginForm;
import org.example.studentmanagementsystem.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UserService userService;
    private final UserSession userSession;

    public LoginController(UserService userService, UserSession userSession) {
        this.userService = userService;
        this.userSession = userSession;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        if (userSession.isLoggedIn()) {
            return "redirect:/home";
        }
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute("loginForm") @Valid LoginForm loginForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("loginForm", loginForm);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.loginForm", bindingResult);
            return "redirect:/login";
        }

        boolean usernameExists = userService.usernameExists(loginForm.getUsername());
        boolean passwordCorrect = usernameExists && userService.isPasswordCorrect(loginForm.getUsername(), loginForm.getPassword());

        if (!usernameExists) {
            redirectAttributes.addFlashAttribute("loginForm", loginForm);
            redirectAttributes.addFlashAttribute("loginError", "Username doesn't exist.");
            return "redirect:/login";
        } else if (!passwordCorrect) {
            redirectAttributes.addFlashAttribute("loginForm", loginForm);
            redirectAttributes.addFlashAttribute("loginError", "Password is incorrect.");
            return "redirect:/login";
        }

        return "redirect:/home";
    }
}
