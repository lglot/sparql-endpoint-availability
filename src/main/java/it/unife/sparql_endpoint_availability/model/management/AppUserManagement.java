package it.unife.sparql_endpoint_availability.model.management;

import it.unife.sparql_endpoint_availability.exception.UserAlreadyExistsException;
import it.unife.sparql_endpoint_availability.exception.UserNotFoundException;
import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AppUserManagement extends UserDetailsService {

    AppUser saveUser(String username, String password, String role) throws UserAlreadyExistsException;
    AppUser getUser(String username);
    List<AppUser> getAllUsers();
    void deleteUser(String username) throws UserNotFoundException;
    void disableUser(String username) throws UserNotFoundException;
    void updateUser(String username, String password, String role);
    // check if at least one user is present in the database
    boolean isEmpty();
    boolean exists(String username);

}
