package com.tasks.taskmanager.users;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication auth) {
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (ga.getAuthority().equals("ROLE_SUPERVISOR")) {
                return "redirect:/supervisor/dashboard";
            } else if (ga.getAuthority().equals("ROLE_EMPLOYEE")) {
                return "redirect:/employee/dashboard";
            }
        }
        return "redirect:/login";
    }
}