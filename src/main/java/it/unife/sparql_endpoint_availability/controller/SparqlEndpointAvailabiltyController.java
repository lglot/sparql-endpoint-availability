package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.service.config.AppConfig;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlListResource;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(path = "/sparql-endpoint-availability")
public class SparqlEndpointAvailabiltyController {

    private final SparqlEndpointManagement sparqlEndpointManagement;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointAvailabiltyController.class);

    @Autowired
    public SparqlEndpointAvailabiltyController(SparqlEndpointManagement sparqlEndpointManagement) {
        this.sparqlEndpointManagement = sparqlEndpointManagement;
    }

    @GetMapping(path = "/update")
    @Transactional
    public @ResponseBody
    String update() {

        /*Get Application Context*/
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        /*Get istance of class that provide access to sparql Endpoint file*/
        SparqlListResource sparqlListResource = ctx.getBean(SparqlListResource.class);

        /*Read spaql endpoint URL from resource and save them to DATA*/
        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointManagement.saveAndGet(sparqlListResource.read());


        SparqlEndpointQueryService sparqlEndpointQueryService = ctx.getBean(SparqlEndpointQueryService.class);
        sparqlEndpointManagement.saveStatuses(sparqlEndpointQueryService.executeQuery(sparqlEndpointList));

        logger.info("Executed manually check in date "+ new Timestamp(System.currentTimeMillis()).toString());
        return "updated";
    }

    @GetMapping("/view")
    @Transactional
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

    @GetMapping("/viewLastUpdate")
    @Transactional
    public @ResponseBody Iterable<SparqlEndpointStatus> getLastUpdate(){
        return sparqlEndpointManagement.getCurrentSparqlStatuses();
    }

    @GetMapping("/view.json")
    @Transactional
    public @ResponseBody
    Iterable<SparqlEndpoint> getAllSparqlStatus() {
        return sparqlEndpointManagement.getAllSparqlStatues();
    }
}

