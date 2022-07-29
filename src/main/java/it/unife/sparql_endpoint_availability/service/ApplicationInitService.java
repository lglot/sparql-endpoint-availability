package it.unife.sparql_endpoint_availability.service;

import it.unife.sparql_endpoint_availability.config.AppConfig;
import it.unife.sparql_endpoint_availability.exception.UserAlreadyExistsException;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@Transactional
public class ApplicationInitService {

    private final AppUserManagement appUserManagement;

    private final AppConfig appConfig;

    private final Logger logger = LoggerFactory.getLogger(ApplicationInitService.class);

    public ApplicationInitService(AppUserManagement appUserManagement, AppConfig appConfig) {
        this.appUserManagement = appUserManagement;
        this.appConfig = appConfig;
    }

    @PostConstruct
    public void init(){
        String adminUsername = appConfig.getAdminUsername();
        String adminPassword = appConfig.getAdminPassword();
        logger.info("Initializing application with admin user: {} and password: {}", adminUsername, adminPassword);

        try {
            appUserManagement.saveUser(adminUsername, adminPassword ,"admin");
        } catch (UserAlreadyExistsException e) {
            logger.info("Admin user already exists: {}", adminUsername);
        }
    }
}
