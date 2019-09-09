package it.unife.sparql_endpoint_availability.controller;


import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
@RequestMapping(path = "/sparql-endpoints")
public class SparqlEndpointAvailabilityRestController {

    private final SparqlEndpointManagement sparqlEndpointManagement;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointAvailabilityController.class);

    @Autowired
    public SparqlEndpointAvailabilityRestController(SparqlEndpointManagement sparqlEndpointManagement) {
        this.sparqlEndpointManagement = sparqlEndpointManagement;
    }


    @GetMapping(path = "")
    public Iterable<SparqlEndpoint.OnlyURL> getSparqlEndpoints() {
        return sparqlEndpointManagement.getAllSE();
    }

    @GetMapping(path = "/{id}")
    public SparqlEndpoint.OnlyURL getSparqlEndpoint(@PathVariable String id) {
        return sparqlEndpointManagement.getSEById(Long.parseLong(id));
    }

    /*@GetMapping(path = "/status")
    public Iterable<SparqlEndpoint> getAllStatusSparqlEndpoints(){
        return sparqlEndpointManagement.getAllSEWithStatus();
    }*/

    @GetMapping(path = "/status/current")
    public Iterable<SparqlEndpoint> getMostRecentStatusSparqlEndpoints() {
        return sparqlEndpointManagement.getSEWithCurrentStatus();
    }

    @GetMapping(path = "/status/current/{id}")
    public SparqlEndpoint getMostRecentStatusSparqlEndpointsById(@PathVariable Long id) {
        return sparqlEndpointManagement.getSEWithCurrentStatusById(id);
    }

    @GetMapping(path = "/status/weekly-history")
    public Iterable<SparqlEndpoint> getWeeklyHistoryStatusSparqlEndpoints() {

        Calendar previousWeek = Calendar.getInstance();
        previousWeek.add(Calendar.WEEK_OF_YEAR, -1);
        logger.info("Requested sparql endopoint availabilty from previuos week " + previousWeek.getTime().toString());
        return sparqlEndpointManagement.getSEWithStatusAfterQueryDate(previousWeek.getTime());
    }

    @GetMapping(path = "/status/current/active")
    public Iterable<SparqlEndpoint> getCurrentlyActiveSparqlEndpoints(){
        return sparqlEndpointManagement.getCurrentlyActiveSE();
    }

}
