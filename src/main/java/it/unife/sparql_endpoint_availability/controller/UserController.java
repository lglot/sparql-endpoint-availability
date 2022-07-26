package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.exception.UserAlreadyExistsException;
import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "", produces = "text/html; charset=UTF-8")
public class UserController {

    AppUserManagement appUserManagement;

    @GetMapping("login")
    public String getLoginView(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
                               @RequestParam(name = "error", required = false, defaultValue = "false") String error,
                               Model model) {

        if (error.equals("true")) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }

        model.addAttribute("lang", lang);

        return "login";
    }

    // Sign in page
    @GetMapping("signup")
    public String getSigninView(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
                               Model model) {
        model.addAttribute("lang", lang);
        return "signup";
    }

    @PostMapping("signup")
    public String signup(@ModelAttribute("user") AppUser user,
                         @RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
                         Model model) {

        log.info("Signing up user: {}", user);
        try{
            appUserManagement.saveUser(user.getUsername(), user.getPassword(), "user");
        } catch (UserAlreadyExistsException e) {
            log.error("User already exists: {}", user);
            model.addAttribute("errorMessage", "User already exists");
            model.addAttribute("lang", lang);
            return "signup";
        }
        model.addAttribute("lang", lang);
        model.addAttribute("successMessage", "User created successfully");
        return "login";
    }
}
