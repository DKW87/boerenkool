package boerenkool.communication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register"; // Dit verwijst naar register.html
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Dit verwijst naar login.html
    }

    @GetMapping("/profile")
    public String showProfilePage() {
        return "profile"; // Dit verwijst naar profile.html
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password"; // Dit verwijst naar forgot-password.html
    }

    @GetMapping("/reset-password-confirm")
    public String showResetPasswordConfirmPage() {
        return "reset-password-confirm"; // Dit verwijst naar forgot-password.html
    }
}
