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
        return "index"; // Dit verwijst naar index.html
    }

    @GetMapping("/profile")
    public String showProfilePage() {
        return "profile"; // Dit verwijst naar profile.html
    }
}
