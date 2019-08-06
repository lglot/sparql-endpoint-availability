package it.unife.sparqlendpoint.service.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages="it.unife.sparqlendpoint.service.thread")
public class Config {

    public static final String SPARQL_ENDPOINTS_LIST_PATH = "C:\\Users\\gigil\\OneDrive\\Università\\Progetti intelliJ\\Tesi_sparql_endpoint\\src\\main\\resources\\sparqlEndpoints_list.txt";
    public static final String SPARQL_ENDPOINTS_LIST_FILENAME="sparqlEndpoints_list.txt";
}
