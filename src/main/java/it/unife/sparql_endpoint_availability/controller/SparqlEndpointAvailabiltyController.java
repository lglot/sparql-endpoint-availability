package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.service.config.AppConfig;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.io.FileUtils.getFile;

@Controller
@RequestMapping(path = "/sparql-endpoint-availability")
public class SparqlEndpointAvailabiltyController {

    private final SparqlEndpointManagement sparqlEndpointManagement;

    @Autowired
    public SparqlEndpointAvailabiltyController(SparqlEndpointManagement sparqlEndpointManagement) {
        this.sparqlEndpointManagement = sparqlEndpointManagement;
    }

    @GetMapping(path = "/update")
    @Transactional
    public @ResponseBody
    String update() {

        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointManagement.saveAndGet(readFromFile());

        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        SparqlEndpointQueryService sparqlEndpointQueryService = ctx.getBean(SparqlEndpointQueryService.class);

        sparqlEndpointManagement.saveStatuses(sparqlEndpointQueryService.executeQuery(sparqlEndpointList));

        return "updated";
    }

    @GetMapping("/view")
    public String view(Model model) {

        HashMap<String, Boolean> sparqlHashMap = new HashMap<>();
        int numberActive = 0;

        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointManagement.getAllSparqlStatues();

        for (SparqlEndpoint sparqlEndpoint : sparqlEndpointList) {
            boolean status = sparqlEndpoint.getSparqlEndpointStatuses().get(sparqlEndpoint.getSparqlEndpointStatuses().size() - 1).isActive();
            sparqlHashMap.put(sparqlEndpoint.getServiceURL(), status);
            if (status) numberActive++;
        }

        model.addAttribute("sparqlHashMap", sparqlHashMap);
        model.addAttribute("numberActive", numberActive);

        return "view";
    }

    @GetMapping("/view.json")
    public @ResponseBody
    Iterable<SparqlEndpoint> getAllSparqlStatus() {
        return sparqlEndpointManagement.getAllSparqlStatues();
    }


    private List<String> readFromFile() {

        List<String> sparqlList = new ArrayList<>();

        try {

            String filePath = Objects.requireNonNull(SparqlEndpointAvailabiltyController.class.getClassLoader().getResource(AppConfig.SPARQL_ENDPOINTS_LIST_FILENAME)).getFile();
            File file = new File(filePath);
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

