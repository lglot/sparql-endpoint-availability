package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.service.config.AppConfig;
import it.unife.sparql_endpoint_availability.service.config.SparqlEndpointStatusConfig;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlListResource;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointQueryService;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;




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
        HashMap<String, String> sparqlHashMap = new HashMap<>();
        int numberActive = 0;
        Date lastUpdate = null;

        Calendar previousWeek = Calendar.getInstance();
        previousWeek.add(Calendar.WEEK_OF_YEAR, -1);

        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointManagement.getSparqlWithStatusAfterQueryDate(previousWeek.getTime());

        if (sparqlEndpointList.size() > 0)
            lastUpdate = sparqlEndpointList.get(0).getSparqlEndpointStatuses().get(0).getQueryDate();

        for (SparqlEndpoint sparqlEndpoint : sparqlEndpointList) {

            if (sparqlEndpoint.getSparqlEndpointStatuses().get(0).isActive()) {
                sparqlHashMap.put(sparqlEndpoint.getServiceURL(), statusConfig.getActive());
                numberActive++;
            } else if (sparqlEndpoint.getSparqlEndpointStatuses().size() > 1) {
                boolean found = false;
                int i = 1;
                while (!found && i < sparqlEndpoint.getSparqlEndpointStatuses().size()) {
                    SparqlEndpointStatus status = sparqlEndpoint.getSparqlEndpointStatuses().get(i);
                    if (status.isActive()) {
                        sparqlHashMap.put(sparqlEndpoint.getServiceURL(), statusConfig.getInactiveLessweek());
                        found = true;
                    }
                    i++;
                }
                if (!found) sparqlHashMap.put(sparqlEndpoint.getServiceURL(), statusConfig.getInactiveMoreweek());
            }
        }

        model.addAttribute("sparqlHashMap", sparqlHashMap);
        model.addAttribute("numberActive", numberActive);
        model.addAttribute("lastUpdate", lastUpdate);
        model.addAttribute("lang",lang);

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

    @GetMapping(path = "/status/all")
    public @ResponseBody
    Iterable<SparqlEndpointStatus> getAllSparqlStatus() {
        return sparqlEndpointManagement.getAllSparqlStatus();
    }
}

