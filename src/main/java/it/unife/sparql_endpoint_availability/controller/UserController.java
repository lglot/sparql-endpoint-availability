package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.dto.UserDto;
import it.unife.sparql_endpoint_availability.exception.UserAlreadyExistsException;
import it.unife.sparql_endpoint_availability.jwt.JwtConfig;
import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping(value = "", produces = "text/html; charset=UTF-8")
public class UserController {

    AppUserManagement appUserManagement;
    JwtConfig jwtConfig;

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
    public String getUserManagementView(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
                              Model model,
                              Authentication authentication) {

        model.addAttribute("lang", lang);

        List<AppUser> allUsers = appUserManagement.getAllUsers();
        model.addAttribute("users", UserDto.fromAppUsers(allUsers));

        return "user_management";
    }

    //delete user by username parameter
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("user/management/delete")
    public String deleteUser(@RequestParam(name = "u", required = true) String username,
                             @RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
                             Model model) {
        model.addAttribute("lang", lang);

        try{
            appUserManagement.deleteUser(username);
        } catch (Exception e) {
            log.error("Error deleting user: {}", username);
            model.addAttribute("errorMessage", "Error deleting user");
        }
        model.addAttribute("successMessage", "User deleted successfully");
        List<AppUser> allUsers = appUserManagement.getAllUsers();
        model.addAttribute("users", UserDto.fromAppUsers(allUsers));
        return "user_management";
    }

    //disable user by username parameter
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("user/management/disable")
    public String disableUser(@RequestParam(name = "u", required = true) String username,
                             @RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
                             Model model) {
        model.addAttribute("lang", lang);

        try{
            appUserManagement.disableUser(username);
        } catch (Exception e) {
            log.error("Error disabling user: {}", username);
            model.addAttribute("errorMessage", "Error disabling user");
        }
        model.addAttribute("successMessage", "User disabled successfully");
        List<AppUser> allUsers = appUserManagement.getAllUsers();
        model.addAttribute("users", UserDto.fromAppUsers(allUsers));
        return "user_management";
    }
}
