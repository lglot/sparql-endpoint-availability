package it.unife.sparql_endpoint_availability.service.resourceManagement.FileImpl;

import it.unife.sparql_endpoint_availability.controller.SparqlEndpointAvailabiltyController;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlListResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SparqlListResourceFileImpl implements SparqlListResource {

    private File file;
    private long lastModified;

    public SparqlListResourceFileImpl(String fileName) {
        String filePath = Objects.requireNonNull(SparqlEndpointAvailabiltyController.class.getClassLoader().getResource(fileName)).getFile();
        file = new File(filePath);
        lastModified = file.lastModified();
    }

    @Override
    public boolean isModified() {
        return lastModified != file.lastModified();
    }

    @Override
    public List<String> read() {

        List<String> sparqlList = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            while ((line = br.readLine()) != null) {
                sparqlList.add(line.toLowerCase());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sparqlList;
    }
}