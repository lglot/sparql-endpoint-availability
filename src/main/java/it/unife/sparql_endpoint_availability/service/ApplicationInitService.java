package it.unife.sparql_endpoint_availability.service;

import it.unife.sparql_endpoint_availability.config.AppConfig;
import it.unife.sparql_endpoint_availability.exception.UserAlreadyExistsException;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@Transactional
public class ApplicationInitService {

    private final AppUserManagement appUserManagement;
    private final AppConfig appConfig;

    private final ApplicationContext ctx;

    private final Logger logger = LoggerFactory.getLogger(ApplicationInitService.class);

    @Autowired
    public ApplicationInitService(AppUserManagement appUserManagement, AppConfig appConfig, ApplicationContext ctx) {
        this.appUserManagement = appUserManagement;
        this.appConfig = appConfig;
        this.ctx = ctx;
    }

    @PostConstruct
    public void init(){
        String adminUsername = appConfig.getAdminUsername();
        String adminPassword = appConfig.getAdminPassword();
        if(adminUsername == null || adminPassword == null || adminUsername.isEmpty() || adminPassword.isEmpty()){
            logger.error("ADMIN USERNAME OR PASSWORD IS NULL OR EMPTY");
            shutdown();
            return;
        }
        logger.info("Initializing application with admin user: {} and password: {}", adminUsername, adminPassword);

        try {
            appUserManagement.saveUser(adminUsername, adminPassword ,"admin");
        } catch (UserAlreadyExistsException e) {
            logger.info("Admin user already exists: {}", adminUsername);
        }
        try {
            appUserManagement.saveUser("luigi" ,"luigi" ,"user");
        } catch (UserAlreadyExistsException e) {
            //ignore
        }
        try {
            appUserManagement.saveUser("mario" ,"mario" ,"user");
        } catch (UserAlreadyExistsException e) {
            //ignore
        }
        try {
            appUserManagement.saveUser("paperino" ,"paperino" ,"user");
        } catch (UserAlreadyExistsException e) {
            //ignore
        }
    }

    //function to shutdown spring application
    public void shutdown(){
        logger.warn("Shutting down application");
        SpringApplication.exit(ctx, () -> -1);
        System.exit(-1);
    }


}