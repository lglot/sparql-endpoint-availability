package it.unife.sparql_endpoint_availability.config;

import it.unife.sparql_endpoint_availability.service.resourceManagement.FileImpl.SparqlEndpointListFileManagementImpl;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlEndpointListFileManagement;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointCheckService;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.sEQueryMultiThreadImpl.SECheckServiceMultiThreadImpl;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.sEQueyTestService.SparqlEndpointCheckServiceTestImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = "it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.sEQueryMultiThreadImpl.thread")
@EnableScheduling
public class AppConfig {

    /*
     * Nome del file che contiene la lista degli URL degli sparql endpoint da
     * controllare
     */
    // private static final String SPARQL_ENDPOINTS_LIST_FILENAME =
    // "sparqlEndpoints_list.txt";
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
        // return new SECheckServiceMultiThreadImpl(QUERY_NUMBER_BY_THREAD);
        return new SparqlEndpointCheckServiceTestImpl();
    }

    @Bean
    public SparqlEndpointListFileManagement getSparqlEndpointListFileManagement() {
        return new SparqlEndpointListFileManagementImpl(SPARQL_ENDPOINTS_LIST_FILENAME);
    }
}
