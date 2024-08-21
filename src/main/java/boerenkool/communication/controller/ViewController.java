package boerenkool.communication.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*Een controller specifiek voor eindgebruikers, zodat ze niet via de restcontroller hoeven te navigeren*/

@Controller
public class ViewController {

    @GetMapping("/profile")
    public String showProfilePage() {
        return "profile"; // Dit verwijst naar src/main/resources/templates/profile.html
    }

}

