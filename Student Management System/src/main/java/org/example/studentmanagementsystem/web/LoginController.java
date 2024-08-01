package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.dtos.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

//    @PostMapping("/login")
//    public String login(
//            @ModelAttribute("loginForm") @Valid LoginForm loginForm,
//            BindingResult bindingResult,
//            RedirectAttributes redirectAttributes
//    ) {
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute("loginForm", loginForm);
//            redirectAttributes.addFlashAttribute(
//                    "org.springframework.validation.BindingResult.loginForm", bindingResult);
//            return "redirect:/login";
//        }
//
//        return "redirect:/home";
//    }
}
