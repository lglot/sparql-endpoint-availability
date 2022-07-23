package it.unife.sparql_endpoint_availability.service;

import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import it.unife.sparql_endpoint_availability.model.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static it.unife.sparql_endpoint_availability.security.ApplicationUserRole.ADMIN;
import static it.unife.sparql_endpoint_availability.security.ApplicationUserRole.USER;

@Component
@Transactional
public class DatabaseInitService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init(){


        AppUser admin = AppUser.builder()
                .username("luigi")
                .password(passwordEncoder.encode("luigi"))
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .authorities(ADMIN.getGrantedAuthorities())
                .build();

        admin.getAuthorities().forEach(ga -> ga.setUser(admin));

        AppUser user = AppUser.builder()
                .username("mario")
                .password(passwordEncoder.encode("mario"))
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .authorities(USER.getGrantedAuthorities())
                .build();

        user.getAuthorities().forEach(ga -> ga.setUser(user));

        if (!appUserRepository.existsByUsername(admin.getUsername())) {
            appUserRepository.save(admin);
        }
        if (!appUserRepository.existsByUsername(user.getUsername())) {
            appUserRepository.save(user);
        }
    }
}
