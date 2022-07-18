package it.unife.sparql_endpoint_availability.service.fileReader.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.service.fileReader.SparqlFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SparqlFileReaderImpl implements SparqlFileReader {

    private File file;

    private static final Logger logger = LoggerFactory.getLogger(SparqlFileReaderImpl.class);

    public SparqlFileReaderImpl(String fileName) {
        try {
            this.file = new ClassPathResource(fileName).getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<SparqlEndpoint> getSparqlEndpoints() {

        ObjectMapper mapper = new ObjectMapper();
        Set<SparqlEndpoint> endpoints;
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
