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
import java.io.FileNotFoundException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

public class SparqlEndpointListFileManagementImpl implements SparqlEndpointListFileManagement {

    private File file;
    private long lastModified;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointListFileManagementImpl.class);

    public SparqlEndpointListFileManagementImpl(String fileName) {
//        String filePath = Objects.requireNonNull(SparqlEndpointAvailabilityController.class.getClassLoader().getResource(fileName)).getFile();
//        ClassPathResource resource = new ClassPathResource(fileName);
//
//        try {
//            file = resource.getFile();
////            file = ResourceUtils.getFile(filePath);
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//        String filePath = Objects.requireNonNull(SparqlEndpointAvailabilityController.class.getClassLoader().getResource(fileName)).getFile();
//        file = new File(filePath);
        file = new File(fileName);
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
