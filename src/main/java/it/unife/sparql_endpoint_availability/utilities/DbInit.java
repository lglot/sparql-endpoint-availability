package it.unife.sparql_endpoint_availability.utilities;

import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import it.unife.sparql_endpoint_availability.model.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static it.unife.sparql_endpoint_availability.security.ApplicationUserRole.ADMIN;
import static it.unife.sparql_endpoint_availability.security.ApplicationUserRole.USER;

@Component
@Transactional
public class DbInit {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DbInit(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //@PostConstruct
    public void init(){

        AppUser luigi = AppUser.builder()
                .username("luigi")
                .password(passwordEncoder.encode("luigi"))
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .authorities(ADMIN.getGrantedAuthorities())
                .build();

        luigi.getAuthorities().forEach(ga -> ga.setUser(luigi));

        AppUser mario = AppUser.builder()
                .username("mario")
                .password(passwordEncoder.encode("mario"))
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .authorities(USER.getGrantedAuthorities())
                .build();

        mario.getAuthorities().forEach(ga -> ga.setUser(mario));

        appUserRepository.save(luigi);
        appUserRepository.save(mario);
    }
}
