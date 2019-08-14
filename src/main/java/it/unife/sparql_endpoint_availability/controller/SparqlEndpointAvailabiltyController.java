package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointStatusRepository;
import it.unife.sparql_endpoint_availability.service.config.Config;
import it.unife.sparql_endpoint_availability.service.thread.SparqlEndpointsQueryThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
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

    private final SparqlEndpointRepository sparqlEndpointRepository;

    private final SparqlEndpointStatusRepository sparqlEndpointStatusRepository;

    public SparqlEndpointAvailabiltyController(SparqlEndpointRepository sparqlEndpointRepository, SparqlEndpointStatusRepository sparqlEndpointStatusRepository) {
        this.sparqlEndpointRepository = sparqlEndpointRepository;
        this.sparqlEndpointStatusRepository = sparqlEndpointStatusRepository;
    }

    @GetMapping(path = "/update")
    public @ResponseBody
    String update() {

        List<SparqlEndpoint> sparqlEndpointList = addSparqlListToData(readFromFile());


        final int queryNumberByThread = Config.QUERY_NUMBER_BY_THREAD;

        /*MultiThread Query*/
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        List<SparqlEndpointsQueryThread> threads = new ArrayList<>();

        for (int i = 0; i < sparqlEndpointList.size(); i = i + queryNumberByThread) {
            SparqlEndpointsQueryThread thread = (SparqlEndpointsQueryThread) context.getBean("sparqlEndpointsQueryThread");
            threads.add(thread);
            thread.setPartialSparqlEndpointsList(sparqlEndpointList.subList(i, Math.min(i + queryNumberByThread, sparqlEndpointList.size())));
            thread.start();
        }

        try {

            List<SparqlEndpointStatus> statusList = new ArrayList<>();

            for (SparqlEndpointsQueryThread thread : threads) {
                thread.join();
                statusList.addAll(thread.getSparqlEndpointStatusList());
                /*List<SparqlEndpointStatus> statusList = new ArrayList<>();
                SparqlEndpointStatus status = new SparqlEndpointStatus();
                sparqlHashMap.putAll(thread.getSparqlHashMap());
                numberActive = numberActive + thread.getNumberActive();*/
            }

            sparqlEndpointStatusRepository.saveAll(statusList);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return "updated";
    }

    @GetMapping("/view")
    public String view(Model model) {

        HashMap<String, Boolean> sparqlHashMap = new HashMap<>();
        int numberActive = 0;

        model.addAttribute("sparqlHashMap", sparqlHashMap);
        model.addAttribute("numberActive", numberActive);

        return "view";
    }


    private List<String> readFromFile() {

        List<String> sparqlList = new ArrayList<>();

        try {

            String filePath = Objects.requireNonNull(SparqlEndpointAvailabiltyController.class.getClassLoader().getResource(Config.SPARQL_ENDPOINTS_LIST_FILENAME)).getFile();
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

    private List<SparqlEndpoint> addSparqlListToData(List<String> sparqlStringList) {

        List<SparqlEndpoint> sparqlEndpointList = new ArrayList<>();

        for (String serviceURL : sparqlStringList) {
            if (!sparqlEndpointRepository.existsSparqlEndpointByServiceURL(serviceURL)) {
                SparqlEndpoint sparqlEndpoint = new SparqlEndpoint();
                sparqlEndpoint.setServiceURL(serviceURL);
                sparqlEndpointList.add(sparqlEndpoint);
            }
        }
        if (sparqlEndpointList.size() > 0)
            sparqlEndpointRepository.saveAll(sparqlEndpointList);

        return (List<SparqlEndpoint>) sparqlEndpointRepository.findAll();
    }
}

