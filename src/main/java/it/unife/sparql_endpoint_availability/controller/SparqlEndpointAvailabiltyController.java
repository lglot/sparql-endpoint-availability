package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatusSummary;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.service.config.AppConfig;
import it.unife.sparql_endpoint_availability.service.config.SparqlEndpointStatusConfig;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlListResource;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointQueryService;
import org.apache.jena.ext.com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping(path = "/sparql-endpoint-availability")
public class SparqlEndpointAvailabiltyController {

    private final SparqlEndpointManagement sparqlEndpointManagement;
    private final SparqlEndpointStatusConfig statusConfig;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointAvailabiltyController.class);

    @Autowired
    public SparqlEndpointAvailabiltyController(SparqlEndpointManagement sparqlEndpointManagement, SparqlEndpointStatusConfig statusConfig) {
        this.sparqlEndpointManagement = sparqlEndpointManagement;
        this.statusConfig = statusConfig;
    }

    @GetMapping(path = "/update")
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

        logger.info("Executed manually check in date " + new Timestamp(System.currentTimeMillis()).toString());
        return "updated";
    }

    @GetMapping(path = "/view")
    public String view(@RequestParam(name="lang",required = false,defaultValue = "en") String lang, Model model) {

        /*HTTP PARAMETERS*/
        HashMap<String, SparqlEndpointStatusSummary> sparqlStatusMap = new HashMap<>();
        int numberActive = 0;
        Date lastUpdate = null;
        Date firstUpdate;
        long weeksPassed = 0;

        Calendar previousWeek = Calendar.getInstance();
        previousWeek.add(Calendar.WEEK_OF_YEAR, -1);

        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointManagement.getSparqlWithStatusAfterQueryDate(previousWeek.getTime());

        if (sparqlEndpointList.size() > 0) {

            List<SparqlEndpointStatus> statusTemp = sparqlEndpointList.get(0).getSparqlEndpointStatuses();
            lastUpdate = sparqlEndpointManagement.findFirstQueryDate();
            firstUpdate = statusTemp.get(statusTemp.size()-1).getQueryDate();
            weeksPassed = ChronoUnit.DAYS.between(lastUpdate.toInstant(), firstUpdate.toInstant())/7;
        }

        /*for (SparqlEndpoint sparqlEndpoint : sparqlEndpointList) {

            SparqlEndpointStatusSummary statusSummary = new SparqlEndpointStatusSummary();

            if (sparqlEndpoint.getSparqlEndpointStatuses().get(0).isActive()) {
               // sparqlStatusMap.put(sparqlEndpoint.getServiceURL(), statusConfig.getActive());
                statusSummary.setStatusString(statusConfig.getActive());
                numberActive++;

            } else if(weeksPassed<1){
                //sparqlStatusMap.put(sparqlEndpoint.getServiceURL(),statusConfig.getGeneralInactive());
                statusSummary.setStatusString(statusConfig.getGeneralInactive());
            }
            if (sparqlEndpoint.getSparqlEndpointStatuses().size() > 1) {

                boolean found = false;
                int i = 1;
                while (!found && i < sparqlEndpoint.getSparqlEndpointStatuses().size()) {
                    SparqlEndpointStatus status = sparqlEndpoint.getSparqlEndpointStatuses().get(i);
                    if (status.isActive()) {
                        sparqlStatusMap.put(sparqlEndpoint.getServiceURL(), statusConfig.getInactiveLessweek());
                        found = true;
                    }
                    i++;
                }
                if (!found) sparqlStatusMap.put(sparqlEndpoint.getServiceURL(), statusConfig.getInactiveMoreweek());
            }
        }*/


        for(SparqlEndpoint sparqlEndpoint : sparqlEndpointList){

            SparqlEndpointStatusSummary statusSummary = new SparqlEndpointStatusSummary();

            double totalStatus = sparqlEndpoint.getSparqlEndpointStatuses().size();
            boolean activeFound = false;
            double uptime = 0;

            if (sparqlEndpoint.getSparqlEndpointStatuses().get(0).isActive()) {
                statusSummary.setStatusString(statusConfig.getActive());
                activeFound=true;
                uptime++;
                numberActive++;
            }
            else if(weeksPassed<1){
                statusSummary.setStatusString(statusConfig.getGeneralInactive());
                activeFound=true;
            }

            for(SparqlEndpointStatus status : Iterables.skip(sparqlEndpoint.getSparqlEndpointStatuses(),1)){

                if(status.isActive()){
                    if(!activeFound){
                        statusSummary.setStatusString(statusConfig.getInactiveLessweek());
                        activeFound=true;
                    }
                    uptime++;
                }
            }
            if(!activeFound) statusSummary.setStatusString(statusConfig.getInactiveMoreweek());
            statusSummary.setUptimelast7d((uptime/totalStatus));
            sparqlStatusMap.put(sparqlEndpoint.getServiceURL(),statusSummary);
        }

        model.addAttribute("sparqlStatusMap", sparqlStatusMap);
        model.addAttribute("numberActive", numberActive);
        model.addAttribute("lastUpdate", lastUpdate);
        model.addAttribute("lang",lang);
        model.addAttribute("weeksPassed",weeksPassed);

        return "view";

    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<SparqlEndpoint.OnlyURL> getSparqlEndpointURL() {
        return sparqlEndpointManagement.getAllSparqlEndopoint();
    }

    @GetMapping(path = "/status/current")
    public @ResponseBody
    Iterable<SparqlEndpoint> getLastUpdate() {
        // return sparqlEndpointManagement.getCurrentSparqlStatus();
        return sparqlEndpointManagement.getSparqlWithCurrentStatus();
    }

    @GetMapping(path = "/status/week")
    public @ResponseBody
    Iterable<SparqlEndpoint> getAfterQueryDate() {

        Calendar previousWeek = Calendar.getInstance();
        previousWeek.add(Calendar.WEEK_OF_YEAR, -1);
        logger.info("Requested sparql endopoint availabilty from previuos week " + previousWeek.getTime().toString());
        return sparqlEndpointManagement.getSparqlWithStatusAfterQueryDate(previousWeek.getTime());
    }

    @GetMapping(path = "/active")
    public @ResponseBody Iterable<SparqlEndpoint> getCurrentlyActive(){
        return sparqlEndpointManagement.getCurrentlyActiveSparqlEndpoints();
    }

    /*@GetMapping(path = "/status/all")
    public @ResponseBody
    Iterable<SparqlEndpointStatus> getAllSparqlStatus() {
        return sparqlEndpointManagement.getAllSparqlStatus();
    }*/
}

