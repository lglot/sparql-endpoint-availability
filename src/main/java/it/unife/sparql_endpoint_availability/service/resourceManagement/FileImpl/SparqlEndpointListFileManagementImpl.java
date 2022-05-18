package it.unife.sparql_endpoint_availability.service.resourceManagement.FileImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlEndpointListFileManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class SparqlEndpointListFileManagementImpl implements SparqlEndpointListFileManagement {

    private InputStream file;
    // private long lastModified;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointListFileManagementImpl.class);

    public SparqlEndpointListFileManagementImpl(String fileName) {
        try {
            file = new ClassPathResource(fileName).getInputStream();
            // lastModified = file.lastModified();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // @Override
    // public boolean isModified() {
    // boolean isModified = lastModified != file.lastModified();
    // if (isModified) {
    // lastModified = file.lastModified();
    // }
    // return isModified;
    // }

    @Override
    public Set<SparqlEndpoint> getSparqlEndpoints() {
        ObjectMapper mapper = new ObjectMapper();
        Set<SparqlEndpoint> endpoints = new HashSet<>();
        try {
            endpoints = mapper.readValue(file, new TypeReference<Set<SparqlEndpoint>>() {
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return endpoints;
    }
}
