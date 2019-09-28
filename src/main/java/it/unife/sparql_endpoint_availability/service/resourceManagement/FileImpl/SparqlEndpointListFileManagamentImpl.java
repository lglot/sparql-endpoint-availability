package it.unife.sparql_endpoint_availability.service.resourceManagement.FileImpl;

import it.unife.sparql_endpoint_availability.controller.SparqlEndpointAvailabilityController;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlEndpointListFileManagament;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SparqlEndpointListFileManagamentImpl implements SparqlEndpointListFileManagament {

    private File file;
    private long lastModified;

    public SparqlEndpointListFileManagamentImpl(String fileName) {
        String filePath = Objects.requireNonNull(SparqlEndpointAvailabilityController.class.getClassLoader().getResource(fileName)).getFile();
        file = new File(filePath);
        lastModified = file.lastModified();
    }

    @Override
    public boolean isModified() {
        return lastModified != file.lastModified();
    }

    @Override
    public List<String> read() {

        Set<String> sparqlSet = new HashSet<>();

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            while ((line = br.readLine()) != null) {
                sparqlSet.add(line.toLowerCase());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(sparqlSet);
    }
}
