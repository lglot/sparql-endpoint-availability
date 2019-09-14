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
import java.util.*;


@Controller
@RequestMapping(path = "/sparql-endpoint-availability")
public class SparqlEndpointAvailabilityController {

    private final SparqlEndpointManagement sparqlEndpointManagement;
    private final SparqlEndpointStatusConfig statusConfig;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointAvailabilityController.class);

    @Autowired
    public SparqlEndpointAvailabilityController(SparqlEndpointManagement sparqlEndpointManagement, SparqlEndpointStatusConfig statusConfig) {
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
        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointManagement.update(sparqlEndpointManagement.getAllSparqlEndpoints(),sparqlListResource.read());

        SparqlEndpointQueryService sparqlEndpointQueryService = ctx.getBean(SparqlEndpointQueryService.class);
        sparqlEndpointManagement.saveStatuses(sparqlEndpointQueryService.executeQuery(sparqlEndpointList));

        logger.info("Executed manually check terminated in date " + new Timestamp(System.currentTimeMillis()).toString());
        return "updated";
    }

    @GetMapping(path = "/view")
    public String view(@RequestParam(name="lang",required = false,defaultValue = "en") String lang, Model model) {

        /*HTTP PARAMETERS*/
        SortedMap<Long, SparqlEndpointStatusSummary> sparqlStatusMap = new TreeMap<>();
        int numberActive = 0;
        Date lastUpdate = null;
        Date firstUpdate;
        long weeksPassed = 0;
        long daysPassed = 0;

        Calendar previousWeek = Calendar.getInstance();
        Calendar previousDay = Calendar.getInstance();
        previousWeek.add(Calendar.WEEK_OF_YEAR, -1);
        previousDay.add(Calendar.DAY_OF_YEAR,-1);

        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointManagement.getSparqlEndpointsAfterQueryDate(previousWeek.getTime());

        if (sparqlEndpointList.size() > 0) {

            List<SparqlEndpointStatus> statusTemp = sparqlEndpointList.get(0).getSparqlEndpointStatuses();
            firstUpdate = sparqlEndpointManagement.findFirstQueryDate();
            lastUpdate  = statusTemp.get(0).getQueryDate();
            daysPassed = ChronoUnit.DAYS.between(firstUpdate.toInstant(), lastUpdate.toInstant());
            weeksPassed = daysPassed/7;
        }

        for(SparqlEndpoint sparqlEndpoint : sparqlEndpointList){

            SparqlEndpointStatusSummary statusSummary = new SparqlEndpointStatusSummary();
            statusSummary.setURL(sparqlEndpoint.getServiceURL());

            List<SparqlEndpointStatus> statusList = sparqlEndpoint.getSparqlEndpointStatuses();

            double totalStatus = statusList.size();
            boolean activeFound = false;
            double activeCounterThisDay = 0;
            double activeCounterThisWeek = 0;

            if (statusList.get(0).isActive()) {
                statusSummary.setStatusString(statusConfig.getActive());
                activeFound=true;
                activeCounterThisDay++;
                activeCounterThisWeek++;
                numberActive++;
            }
            else if(weeksPassed<1){
                statusSummary.setStatusString(statusConfig.getGeneralInactive());
                activeFound=true;
            }

            int i=1;
            Date yesterday = previousDay.getTime();

            while(statusList.get(i).getQueryDate().after(yesterday) && i<totalStatus) {

                SparqlEndpointStatus status=statusList.get(i);

                if(status.isActive()){
                    if(!activeFound){
                        statusSummary.setStatusString(statusConfig.getInactiveLessday());
                        activeFound=true;
                    }
                    activeCounterThisDay++;
                    activeCounterThisWeek++;
                }
                i++;
            }

            int totalStatusThisDay = i;

            for(SparqlEndpointStatus status : Iterables.skip(statusList,totalStatusThisDay)){

                if(status.isActive()){
                    if(!activeFound){
                        statusSummary.setStatusString(statusConfig.getInactiveLessweek());
                        activeFound=true;
                    }
                    activeCounterThisWeek++;
                }
            }
            if(!activeFound) statusSummary.setStatusString(statusConfig.getInactiveMoreweek());
            statusSummary.setUptimelast7d((activeCounterThisWeek/totalStatus));
            statusSummary.setUptimeLast24h(activeCounterThisDay/totalStatusThisDay);
            sparqlStatusMap.put(sparqlEndpoint.getId(),statusSummary);
        }

        model.addAttribute("sparqlStatusMap", sparqlStatusMap);
        model.addAttribute("numberActive", numberActive);
        model.addAttribute("lastUpdate", lastUpdate);
        model.addAttribute("lang",lang);
        model.addAttribute("daysPassed",daysPassed);

        return "view";

    }

}

