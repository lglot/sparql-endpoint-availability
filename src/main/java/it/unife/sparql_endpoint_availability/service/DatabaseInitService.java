package it.unife.sparql_endpoint_availability.service;

import it.unife.sparql_endpoint_availability.exception.UserAlreadyExistsException;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import it.unife.sparql_endpoint_availability.model.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@Transactional
public class DatabaseInitService {

    private final AppUserManagement appUserManagement;

    private final Logger logger = LoggerFactory.getLogger(DatabaseInitService.class);

    public DatabaseInitService(AppUserManagement appUserManagement) {
        this.appUserManagement = appUserManagement;
    }

    @PostConstruct
    public void init(){
        try {
            appUserManagement.saveUser("luigi", "luigi" ,"admin");
            appUserManagement.saveUser("mario", "mario", "user");
        } catch (UserAlreadyExistsException e) {
            logger.info("Database already initialized");
        }
    }
}
