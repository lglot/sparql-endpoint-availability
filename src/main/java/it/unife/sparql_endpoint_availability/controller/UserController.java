package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.dto.UserDto;
import it.unife.sparql_endpoint_availability.exception.UserAlreadyExistsException;
import it.unife.sparql_endpoint_availability.jwt.JwtConfig;
import it.unife.sparql_endpoint_availability.jwt.TokenManager;
import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.List;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping(value = "", produces = "text/html; charset=UTF-8")
public class UserController {

    AppUserManagement appUserManagement;
    JwtConfig jwtConfig;
    private final SecretKey secretKey;


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
    public String getSignupView(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
            Model model) {
        model.addAttribute("lang", lang);
        return "signup";
    }

    @PostMapping("signup")
    public String signup(@ModelAttribute("user") AppUser user,
            @RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
            Model model) {

        log.info("Signing up user: {}", user);
        try {
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

    @GetMapping("user")
    public String getUserView(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
            Model model,
            Authentication authentication) {

        AppUser user = (AppUser) authentication.getPrincipal();
        UserDto userDto = UserDto.fromAppUser(user);


        model.addAttribute("prefix_jwt", jwtConfig.getPrefix());
        model.addAttribute("user", userDto);
        model.addAttribute("lang", lang);

        return "user_view";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("user/management")
    public String manageUser(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "u", required = false) String username,
            Model model,
            Authentication authentication) {

        if (action != null) {
            try {
                AppUser loggedAdmin = (AppUser) authentication.getPrincipal();
                if (loggedAdmin.getUsername().equals(username)) {
                    model.addAttribute("errorMessage", "You cannot " + action + " yourself");
                } else {
                    switch (action) {
                        case "delete":
                            appUserManagement.deleteUser(username);
                            break;
                        case "disable":
                            appUserManagement.disableUser(username);
                            break;
                        case "enable":
                            appUserManagement.enableUser(username);
                            break;
                    }
                    model.addAttribute("successMessage", "user.management." + action + ".success");
                }
            } catch (Exception e) {
                log.error("Error action " + action + " user: {}", username);
                model.addAttribute("errorMessage", "user.management." + action + ".error");
            }
        }
        List<AppUser> allUsers = appUserManagement.getAllUsers();
        model.addAttribute("users", UserDto.fromAppUsers(allUsers));
        model.addAttribute("lang", lang);
        return "user_management";
    }

    // generate new JWT token
    @GetMapping("user/getToken")
    public String generateNewToken(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
            Model model,
            Authentication authentication) {

        AppUser user = (AppUser) authentication.getPrincipal();
        // generate new token
        String token = TokenManager.createToken(
                user.getUsername(),
                user.getAuthorities(),
                jwtConfig.getExpirationTimeAfertDays(),
                secretKey);


        UserDto userDto = UserDto.fromAppUser(user);
        userDto.setJwtToken(token);

        model.addAttribute("prefix_jwt", jwtConfig.getPrefix());
        model.addAttribute("jwt_expiration_days", jwtConfig.getExpirationTimeAfertDays());
        model.addAttribute("user", userDto);
        model.addAttribute("lang", lang);

        return "user_view";
    }
}
