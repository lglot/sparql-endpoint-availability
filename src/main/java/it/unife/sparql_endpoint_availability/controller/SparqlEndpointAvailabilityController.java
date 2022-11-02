package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.dto.SparqlEndpointStatusDTO;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatusLabel.ACTIVE;
import static it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatusLabel.UNKNOWN;

//Classe controller che gestisce le richieste del client
@Controller
@RequestMapping(path = "", produces = "text/html")
public class SparqlEndpointAvailabilityController {

    private final SparqlEndpointManagement sparqlEndpointManagement;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointAvailabilityController.class);

    public SparqlEndpointAvailabilityController(SparqlEndpointManagement sparqlEndpointManagement) {
        this.sparqlEndpointManagement = sparqlEndpointManagement;
    }

    @GetMapping(path = "")
    public String view(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang, Model model) {

        commonView(model);
        model.addAttribute("lang", lang);
        return "view";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/management")
    public String manageSparqlEndpoint(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
                                        @RequestParam(name = "action", required = false) String action,
                                        @RequestParam(name = "url", required = false) String url,
                                        Model model,
                                        Authentication authentication) {
        if (action != null) {
            try{
                if (action.equals("delete")) {
                    sparqlEndpointManagement.deleteSparqlEndpointByUrl(url);
                }
                model.addAttribute("successMessage", "endpoint.management."+action+".success");
            }
            catch (Exception e) {
                logger.error("Error action "+action+" endpoint: {}", url);
                model.addAttribute("errorMessage", "endpoint.management."+action+".error");
            }
        }
        commonView(model);
        model.addAttribute("lang", lang);
        return "view";
    }

    //create a new endpoint
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public String createSparqlEndpoint(@ModelAttribute("sparqlEndpoint") SparqlEndpoint sparqlEndpoint,
                                        @RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
                                        @RequestParam(name = "url", required = false) String url,
                                        Model model,
                                        Authentication authentication) {
        try{
            sparqlEndpointManagement.createSparqlEndpoint(sparqlEndpoint);
            model.addAttribute("successMessage", "endpoint.management.create.success");
        }
        catch (Exception e) {
            logger.error("Error action create endpoint: {}", url);
            model.addAttribute("errorMessage", "endpoint.management.create.error");
        }
        commonView(model);
        model.addAttribute("lang", lang);
        return "view";
    }

    private void commonView(Model model){

        /* HTTP PARAMETERS */
        SortedMap<Long, SparqlEndpointStatusDTO> sparqlStatusMap = new TreeMap<>();
        LocalDateTime lastUpdate = null;
        int numberActive = 0;
        LocalDateTime lastUpdateLocal = null;
        long weeksPassed;
        long daysPassed = 0;
        String applicationMessage = null;


        /* GET SPARQL ENDPOINTS AFTER LAST WEEK */
        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointManagement.getSparqlEndpointsAfterLastWeek();

        if (sparqlEndpointList.size() == 0 || sparqlEndpointList.get(0).getSparqlEndpointStatuses().size() == 0)
            applicationMessage = "No SPARQL endpoint DATA found";
        else {
            sparqlEndpointList.forEach(sparqlEndpoint -> {
                sparqlStatusMap.put(sparqlEndpoint.getId(),
                        sparqlEndpoint.getSparqlEndpointStatuses().size() > 0 ?
                                SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(sparqlEndpoint.getSparqlEndpointStatuses(), true)
                        : SparqlEndpointStatusDTO.builder()
                                .url(sparqlEndpoint.getUrl())
                                .name(sparqlEndpoint.getName())
                                .status(UNKNOWN.getLabel())
                                .uptimelast7d(-1)
                                .uptimeLast24h(-1)
                                .build());
            });

            numberActive = (int) sparqlStatusMap.values().stream().filter(s -> s.getStatus().equals(ACTIVE.getLabel())).count();
            lastUpdate = sparqlEndpointList.get(0).getLastCheckDate(true);
            lastUpdateLocal = lastUpdate.atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.MINUTES);
        }
        String lastUpdateLocalString = "No date";
        if (lastUpdateLocal != null) {
            lastUpdateLocalString = lastUpdateLocal.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            lastUpdateLocalString = lastUpdateLocalString.split("T")[0] + " " + lastUpdateLocalString.split("T")[1];
        }

        model.addAttribute("applicationMessage", applicationMessage);
        model.addAttribute("sparqlStatusMap", sparqlStatusMap);
        model.addAttribute("numberActive", numberActive);
        model.addAttribute("lastUpdate", lastUpdate);

        model.addAttribute("lastUpdateLocal", lastUpdateLocalString);

    }

}
