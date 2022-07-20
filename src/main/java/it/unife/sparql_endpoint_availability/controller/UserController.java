package it.unife.sparql_endpoint_availability.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "", produces = "text/html; charset=UTF-8")
public class UserController {

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
}
