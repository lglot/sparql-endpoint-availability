package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.dto.SparqlEndpointDTO;
import it.unife.sparql_endpoint_availability.exception.SparqlEndpointAlreadyExistsException;
import it.unife.sparql_endpoint_availability.exception.SparqlEndpointNotFoundException;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import org.apache.jena.ext.com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(path = "/api/endpoints", produces = "application/json")
public class SparqlEndpointAvailabilityRestController {

    private final SparqlEndpointDATAManagement sparqlEndpointDATAManagement;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointAvailabilityController.class);

    @Autowired
    public SparqlEndpointAvailabilityRestController(SparqlEndpointDATAManagement sparqlEndpointDATAManagement) {
        this.sparqlEndpointDATAManagement = sparqlEndpointDATAManagement;
    }

    @GetMapping(path = "")
    public Iterable<SparqlEndpointDTO> getAllSparqlEndpoints() {
        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointDATAManagement.getSparqlEndpointsWithCurrentStatus();
        return SparqlEndpointDTO.fromSparqlEndpointList(sparqlEndpointList);
    }

    @GetMapping(path = "/{id}")
    public SparqlEndpointDTO getSparqlEndpointById(@PathVariable @NotNull Long id)  {
      try{
          SparqlEndpoint se = sparqlEndpointDATAManagement.getSparqlEndpointWithCurrentStatusById(id);
          return SparqlEndpointDTO.fromSparqlEndpoint(se);
      } catch (SparqlEndpointNotFoundException e) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sparql Endpoint not found", e);
      }
    }

    @GetMapping(path = "/url")
    public SparqlEndpointDTO getSparqlEndpointByUrl(@RequestParam @NotNull String url) {
        try {
            url = URLDecoder.decode(url, Charsets.UTF_8.name());
            SparqlEndpoint se = sparqlEndpointDATAManagement.getSparqlEndpointByUrl(url);
            return SparqlEndpointDTO.fromSparqlEndpoint(se);
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SparqlEndpoint not found");
        }
    }

    @PostMapping(path = "")
    public ResponseEntity<SparqlEndpointDTO> createSparqlEndpoint(@RequestBody @NotNull SparqlEndpoint sparqlEndpoint) {
        try{
            SparqlEndpoint se = sparqlEndpointDATAManagement.createSparqlEndpoint(sparqlEndpoint);
            logger.info("SparqlEndpoint created: {}", se);
            return new ResponseEntity<>(SparqlEndpointDTO.fromSparqlEndpoint(se), HttpStatus.CREATED);
        } catch (SparqlEndpointAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SparqlEndpoint already exists", e);
        }
    }
    @PutMapping(path = "/url")
    public SparqlEndpointDTO updateSparqlEndpointByUrl(@RequestParam @NotNull String url, @RequestBody @NotNull SparqlEndpoint sparqlEndpoint) {
        try {
            url = URLDecoder.decode(url, Charsets.UTF_8.name());
            SparqlEndpoint result = sparqlEndpointDATAManagement.updateSparqlEndpointByUrl(url, sparqlEndpoint);
            logger.info("SparqlEndpoint updated: {}", result);
            return SparqlEndpointDTO.fromSparqlEndpoint(result);
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SparqlEndpoint not found");

        }
    }

    @DeleteMapping(path = "/url")
    public void deleteSparqlEndpointByUrl(@RequestParam @NotNull String url) {
        try {
            url = URLDecoder.decode(url, Charsets.UTF_8.name());
            sparqlEndpointDATAManagement.deleteSparqlEndpointByUrl(url);
            logger.info("SparqlEndpoint deleted: {}", url);
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SparqlEndpoint not found");
        }
    }



    @GetMapping(path = "/status/current")
    public Iterable<SparqlEndpoint> getSparqlEndpointsWithCurrentStatus() {
        return sparqlEndpointDATAManagement.getSparqlEndpointsWithCurrentStatus();
    }

    @GetMapping(path = "/status/current/active")
    public Iterable<SparqlEndpointDTO> getCurrentlyActiveSparqlEndpoints() {
        return SparqlEndpointDTO.fromSparqlEndpointList(sparqlEndpointDATAManagement.getCurrentlyActiveSparqlEndpoints());
    }


    @GetMapping(path = "/status/weekly-history")
    public Iterable<SparqlEndpoint> getWeeklyHistoryStatusSparqlEndpoints() {

        Calendar previousWeek = Calendar.getInstance();
        previousWeek.add(Calendar.WEEK_OF_YEAR, -1);
        logger.info("Requested sparql endopoint availabilty from previuos week " + previousWeek.getTime());
        return sparqlEndpointDATAManagement.getSparqlEndpointsAfterQueryDate(previousWeek.getTime());
    }

    @GetMapping(path = "/status/weekly-history/{id}")
    public SparqlEndpoint getWeeklyHistoryStatusSparqlEndpointById(@PathVariable @NotNull Long id) {

        Calendar previousWeek = Calendar.getInstance();
        previousWeek.add(Calendar.WEEK_OF_YEAR, -1);
        logger.info("Requested sparql endopoint availabilty from previuos week " + previousWeek.getTime());
        try{
            return sparqlEndpointDATAManagement.getSparqlEndpointsAfterQueryDateById(previousWeek.getTime(), id);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping(path = "/status/daily-history")
    public Iterable<SparqlEndpoint> getDailyHistoryStatusSparqlEndpoints() {

        Calendar previousDay = Calendar.getInstance();
        previousDay.add(Calendar.DAY_OF_YEAR, -1);
        logger.info("Requested sparql endopoint availabilty from previuos day " + previousDay.getTime());
        return sparqlEndpointDATAManagement.getSparqlEndpointsAfterQueryDate(previousDay.getTime());
    }

    @GetMapping(path = "/status/daily-history/{id}")
    public SparqlEndpoint getDailyHistoryStatusSparqlEndpointById(@PathVariable @NotNull Long id) {

        Calendar previousDay = Calendar.getInstance();
        previousDay.add(Calendar.DAY_OF_YEAR, -1);
        logger.info("Requested sparql endopoint availabilty from previuos day " + previousDay.getTime());
        try{
            return sparqlEndpointDATAManagement.getSparqlEndpointsAfterQueryDateById(previousDay.getTime(), id);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SparqlEndpoint not found");
        }
    }

}
