package it.unife.sparql_endpoint_availability.config;

import it.unife.sparql_endpoint_availability.service.filereader.impl.SparqlFileReaderImpl;
import it.unife.sparql_endpoint_availability.service.filereader.SparqlFileReader;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointCheck.SparqlEndpointCheckService;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointCheck.impl.multiThreadImpl.SparqlEndpointCheckMultiThreadImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "it.unife.sparql_endpoint_availability.service.sparqlEndpointCheck")
@EnableTransactionManagement
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private String adminPassword;
    private String adminUsername;

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

    /* Nome del file che contiene la lista degli sparql endpoint da controllare */
    private static final String SPARQL_ENDPOINTS_LIST_FILENAME = "sparql_endpoints.json";

    /*
     * numero di query da eseuire per ogni thread creato per eseguire il controllo
     * dellla disponibilità
     */
    private static final int QUERY_NUMBER_BY_THREAD = 4;

    public static final String LOCAL_TIMEZONE = "Europe/Rome";

    /* Iniettori delle dipendenze */
    @Bean
    public SparqlEndpointCheckService getSparqlEndpointCheckService() {
        return new SparqlEndpointCheckMultiThreadImpl(QUERY_NUMBER_BY_THREAD);
    }

    @Bean
    public SparqlFileReader getSparqlFileReader() {
        return new SparqlFileReaderImpl(SPARQL_ENDPOINTS_LIST_FILENAME);
    }
}
