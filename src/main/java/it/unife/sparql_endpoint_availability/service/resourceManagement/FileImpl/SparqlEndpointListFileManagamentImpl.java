package it.unife.sparql_endpoint_availability.service.resourceManagement.FileImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unife.sparql_endpoint_availability.controller.SparqlEndpointAvailabilityController;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlEndpointListFileManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlEndpointListFileManagamentImpl implements SparqlEndpointListFileManagement {

    private File file;
    private long lastModified;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointListFileManagamentImpl.class);

    public SparqlEndpointListFileManagamentImpl(String fileName) {
        String filePath = Objects.requireNonNull(SparqlEndpointAvailabilityController.class.getClassLoader().getResource(fileName)).getFile();
        file = new File(filePath);
        lastModified = file.lastModified();
    }

    @Override
    public boolean isModified() {
        boolean isModified = lastModified != file.lastModified();
        if (isModified) {
            lastModified = file.lastModified();
        }
        return isModified;
    }

//    @Override
//    public List<String> listSPARQLEndpoints() {
//
//        Set<String> sparqlSet = new HashSet<>();
//
//        try {
//            FileReader fileReader = new FileReader(file);
//            BufferedReader br = new BufferedReader(fileReader);
//            String line;
//            while ((line = br.readLine()) != null) {
//                sparqlSet.add(line.toLowerCase());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return new ArrayList<>(sparqlSet);
//    }
    
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
