package it.unife.sparql_endpoint_availability.service.config;


import it.unife.sparql_endpoint_availability.service.resourceManagement.FileImpl.SparqlListResourceFileImpl;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlListResource;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointQueryService;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.sEQueryMultiThreadImpl.SEQueryServiceMultiThreadImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = "it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.sEQueryMultiThreadImpl.thread")
@EnableScheduling
public class AppConfig {

    private static final String SPARQL_ENDPOINTS_LIST_FILENAME = "sparqlEndpoints_list.txt";
    private static final int QUERY_NUMBER_BY_THREAD = 5;
    public static final String LOCAL_TIMEZONE = "Europe/Rome";

    @Bean
    public SparqlEndpointQueryService getSparqlEndpointQueryService(){
        return new SEQueryServiceMultiThreadImpl(QUERY_NUMBER_BY_THREAD);
    }

    @Bean
    public SparqlListResource getSparqlListResource(){
        return new SparqlListResourceFileImpl(SPARQL_ENDPOINTS_LIST_FILENAME);
    }
}
