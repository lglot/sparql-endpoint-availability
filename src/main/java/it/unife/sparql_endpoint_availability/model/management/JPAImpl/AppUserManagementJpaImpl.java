package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import it.unife.sparql_endpoint_availability.exception.UserAlreadyExistsException;
import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import it.unife.sparql_endpoint_availability.model.repository.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static it.unife.sparql_endpoint_availability.security.ApplicationUserRole.ADMIN;
import static it.unife.sparql_endpoint_availability.security.ApplicationUserRole.USER;

@Service
@Transactional
@Slf4j
public class AppUserManagementJpaImpl implements AppUserManagement {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserManagementJpaImpl(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param username the username of the user to load
     * @return  the user details object for the given username
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    @Override
    public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)) ;
    }

    @Override
    public AppUser saveUser(String username, String password, String role) throws UserAlreadyExistsException {
        AppUser user = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .build();
        if (role.equalsIgnoreCase("admin")) {
            user.setAuthorities(ADMIN.getGrantedAuthorities());
        } else if (role.equalsIgnoreCase("user")) {
            user.setAuthorities(USER.getGrantedAuthorities());
        }
        else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        user.getAuthorities().forEach(ga -> ga.setUser(user));
        if (appUserRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(username);
        }
        log.info("Saving new user: {}", user);
        return appUserRepository.save(user);
    }

    @Override
    public AppUser getUser(String username) {
        return loadUserByUsername(username);
    }


    @Override
    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }

    @Override
    public boolean exists(String username) {
        return appUserRepository.existsByUsername(username);
    }
}
