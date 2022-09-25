package it.unife.sparql_endpoint_availability.config;

import it.unife.sparql_endpoint_availability.service.SparqlEndpointCheckService;
import it.unife.sparql_endpoint_availability.service.SparqlEndpointsFileService;
import it.unife.sparql_endpoint_availability.service.impl.SparqlEndpointFakeCheckService;
import it.unife.sparql_endpoint_availability.service.impl.SparqlEndpointsFileServiceImpl;
import it.unife.sparql_endpoint_availability.service.impl.sparqlEndpointsMultiThreadCheck.SparqlEndpointMultiThreadCheckService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "it.unife.sparql_endpoint_availability.service.impl")
@EnableTransactionManagement
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String adminPassword;
    private String adminUsername;

    /* Nome del file che contiene la lista degli sparql endpoint da controllare */
    private static final String SPARQL_ENDPOINTS_LIST_FILENAME = "sparql_endpoints.json";

    /*
     * numero di query da eseuire per ogni thread creato per eseguire il controllo
     * dellla disponibilità
     */
    private int queryNumberByThread;

    private boolean fakeCheck = false;

    public static final String LOCAL_TIMEZONE = "Europe/Rome";

    /* Iniettori delle dipendenze */
    @Bean
    public SparqlEndpointCheckService getSparqlEndpointCheckService() {
        if (fakeCheck) {
            return new SparqlEndpointFakeCheckService();
        } else {
            return new SparqlEndpointMultiThreadCheckService(queryNumberByThread);
        }
    }

    @Bean
    public SparqlEndpointsFileService getSparqlFileReader() {
        return new SparqlEndpointsFileServiceImpl(SPARQL_ENDPOINTS_LIST_FILENAME);
    }


    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public int getQueryNumberByThread() {
        return queryNumberByThread;
    }

    public void setQueryNumberByThread(int queryNumberByThread) {
        this.queryNumberByThread = queryNumberByThread;
    }

    public boolean isFakeCheck() {
        return fakeCheck;
    }

    public void setFakeCheck(boolean fakeCheck) {
        this.fakeCheck = fakeCheck;
    }

}
