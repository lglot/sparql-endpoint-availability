package it.unife.sparqlendpoint.service.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages="it.unife.sparqlendpoint.service.thread")
public class Config {
    public static final String SPARQL_ENDPOINTS_LIST_FILENAME="sparqlEndpoints_list.txt";
}
