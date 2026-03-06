package org.example.demo_secu_basic_webflux;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

    @GetMapping("/public/hello")
    public String hello() {
        return "Endpoint Public";
    }

    @GetMapping("/user/hello")
    public String helloUser() {
        return "Endpoint utilisateur toto ";
    }

    @GetMapping("/admin/hello")
    public String helloAdmin() {
        return "Endpoint administrateur tata ";
    }
}
