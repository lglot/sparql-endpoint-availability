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

import javax.validation.constraints.NotNull;
import java.util.Calendar;

@RestController
@RequestMapping(path = "/sparql-endpoint")
public class SparqlEndpointAvailabilityRestController {

    private final SparqlEndpointManagement sparqlEndpointManagement;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointAvailabilityController.class);

    @Autowired
    public SparqlEndpointAvailabilityRestController(SparqlEndpointManagement sparqlEndpointManagement) {
        this.sparqlEndpointManagement = sparqlEndpointManagement;
    }


    @GetMapping(path = "")
    public Iterable<SparqlEndpoint.OnlyURL> getURLSparqlEndpoints() {
        return sparqlEndpointManagement.getAllURLSparqlEndpoints();
    }

    @GetMapping(path = "/{id}")
    public SparqlEndpoint.OnlyURL getURLSparqlEndpoint(@PathVariable @NotNull Long id) {
        return sparqlEndpointManagement.getURLSparqlEndpointById(id);
    }

    /*@GetMapping(path = "/status")
    public Iterable<SparqlEndpoint> getAllStatusSparqlEndpoints(){
        return sparqlEndpointManagement.getAllSparqlEndpoints();
    }*/

    @GetMapping(path = "/status/current")
    public Iterable<SparqlEndpoint> getMostRecentStatusSparqlEndpoints() {
        return sparqlEndpointManagement.getSparqlEndpointsWithCurrentStatus();
    }

    @GetMapping(path = "/status/current/active")
    public Iterable<SparqlEndpoint> getCurrentlyActiveSparqlEndpoints(){
        return sparqlEndpointManagement.getCurrentlyActiveSparqlEndpoints();
    }

    @GetMapping(path = "/status/current/{id}")
    public SparqlEndpoint getMostRecentStatusSparqlEndpointById(@PathVariable @NotNull Long id) {
        return sparqlEndpointManagement.getSparqlEndpointWithCurrentStatusById(id);
    }

    @GetMapping(path = "/status/weekly-history")
    public Iterable<SparqlEndpoint> getWeeklyHistoryStatusSparqlEndpoints() {

        Calendar previousWeek = Calendar.getInstance();
        previousWeek.add(Calendar.WEEK_OF_YEAR, -1);
        logger.info("Requested sparql endopoint availabilty from previuos week " + previousWeek.getTime().toString());
        return sparqlEndpointManagement.getSparqlEndpointsAfterQueryDate(previousWeek.getTime());
    }

    @GetMapping(path = "/status/weekly-history/{id}")
    public SparqlEndpoint getWeeklyHistoryStatusSparqlEndpointById(@PathVariable @NotNull Long id) {

        Calendar previousWeek = Calendar.getInstance();
        previousWeek.add(Calendar.WEEK_OF_YEAR, -1);
        logger.info("Requested sparql endopoint availabilty from previuos week " + previousWeek.getTime().toString());
        return sparqlEndpointManagement.getSparqlEndpointsAfterQueryDateById(previousWeek.getTime(),id);
    }

    @GetMapping(path = "/status/daily-history")
    public Iterable<SparqlEndpoint> getDailyHistoryStatusSparqlEndpoints(){

        Calendar previousDay = Calendar.getInstance();
        previousDay.add(Calendar.DAY_OF_YEAR,-1);
        logger.info("Requested sparql endopoint availabilty from previuos day " + previousDay.getTime().toString());
        return sparqlEndpointManagement.getSparqlEndpointsAfterQueryDate(previousDay.getTime());
    }

    @GetMapping(path = "/status/daily-history/{id}")
    public SparqlEndpoint getDailyHistoryStatusSparqlEndpointById(@PathVariable @NotNull Long id){

        Calendar previousDay = Calendar.getInstance();
        previousDay.add(Calendar.DAY_OF_YEAR,-1);
        logger.info("Requested sparql endopoint availabilty from previuos day " + previousDay.getTime().toString());
        return sparqlEndpointManagement.getSparqlEndpointsAfterQueryDateById(previousDay.getTime(),id);
    }



}
