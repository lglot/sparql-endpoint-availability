package it.unife.sparql_endpoint_availability.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.service.SparqlEndpointsFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class SparqlEndpointsFileServiceImpl implements SparqlEndpointsFileService {

    private InputStream sparqlFile;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointsFileServiceImpl.class);

    public SparqlEndpointsFileServiceImpl(String fileName) {
        try {
            this.sparqlFile = new ClassPathResource(fileName).getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<SparqlEndpoint> getSparqlEndpoints() {

        ObjectMapper mapper = new ObjectMapper();
        Set<SparqlEndpoint> endpoints;
        try {
            endpoints = mapper.readValue(sparqlFile, new TypeReference<Set<SparqlEndpoint>>() {
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return endpoints;
    }

}
