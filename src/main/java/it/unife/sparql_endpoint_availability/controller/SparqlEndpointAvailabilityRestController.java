package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.dto.SparqlEndpointDTO;
import it.unife.sparql_endpoint_availability.exception.SparqlEndpointAlreadyExistsException;
import it.unife.sparql_endpoint_availability.exception.SparqlEndpointNotFoundException;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import org.apache.jena.ext.com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/api/endpoints", produces = "application/json")
public class SparqlEndpointAvailabilityRestController {

    private final SparqlEndpointManagement sparqlEndpointManagement;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointAvailabilityController.class);

    @Autowired
    public SparqlEndpointAvailabilityRestController(SparqlEndpointManagement sparqlEndpointManagement) {
        this.sparqlEndpointManagement = sparqlEndpointManagement;
    }

    @GetMapping(path = "")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Iterable<SparqlEndpointDTO> getAllSparqlEndpoints() {
        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointManagement.getSparqlEndpointsWithCurrentStatus();
        return SparqlEndpointDTO.fromSparqlEndpointList(sparqlEndpointList);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public SparqlEndpointDTO getSparqlEndpointById(@PathVariable @NotNull Long id)  {
      try{
          SparqlEndpoint se = sparqlEndpointManagement.getSparqlEndpointById(id);
          return SparqlEndpointDTO.fromSparqlEndpoint(se);
      } catch (SparqlEndpointNotFoundException e) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sparql Endpoint not found", e);
      }
    }

    @GetMapping(path = "/url")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public SparqlEndpointDTO getSparqlEndpointByUrl(@RequestParam @NotNull String url) {
        try {
            url = URLDecoder.decode(url, Charsets.UTF_8.name());
            SparqlEndpoint se = sparqlEndpointManagement.getSparqlEndpointByUrl(url);
            return SparqlEndpointDTO.fromSparqlEndpoint(se);
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SparqlEndpoint not found");
        }
    }

    @PostMapping(path = "")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SparqlEndpointDTO> createSparqlEndpoint(@RequestBody @NotNull SparqlEndpoint sparqlEndpoint) {
        try{
            SparqlEndpoint se = sparqlEndpointManagement.createSparqlEndpoint(sparqlEndpoint);
            logger.info("SparqlEndpoint created: {}", se);
            return new ResponseEntity<>(SparqlEndpointDTO.fromSparqlEndpoint(se), HttpStatus.CREATED);
        } catch (SparqlEndpointAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SparqlEndpoint already exists", e);
        }
    }
    @PutMapping(path = "/url")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public SparqlEndpointDTO updateSparqlEndpointByUrl(@RequestParam @NotNull String url, @RequestBody @NotNull SparqlEndpoint sparqlEndpoint) {
        try {
            url = URLDecoder.decode(url, Charsets.UTF_8.name());
            SparqlEndpoint result = sparqlEndpointManagement.updateSparqlEndpointByUrl(url, sparqlEndpoint);
            logger.info("SparqlEndpoint updated: {}", result);
            return SparqlEndpointDTO.fromSparqlEndpoint(result);
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SparqlEndpoint not found");

        }
    }

    @DeleteMapping(path = "/url")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteSparqlEndpointByUrl(@RequestParam @NotNull String url) {
        try {
            url = URLDecoder.decode(url, Charsets.UTF_8.name());
            sparqlEndpointManagement.deleteSparqlEndpointByUrl(url);
            logger.info("SparqlEndpoint deleted: {}", url);
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SparqlEndpoint not found");
        }
    }



    @GetMapping(path = "/status/current")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Iterable<SparqlEndpoint> getSparqlEndpointsWithCurrentStatus() {
        return sparqlEndpointManagement.getSparqlEndpointsWithCurrentStatus();
    }

    @GetMapping(path = "/status/current/active")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Iterable<SparqlEndpointDTO> getCurrentlyActiveSparqlEndpoints() {
        return SparqlEndpointDTO.fromSparqlEndpointList(sparqlEndpointManagement.getCurrentlyActiveSparqlEndpoints());
    }


    @GetMapping(path = "/status/{days}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Iterable<SparqlEndpoint> getHistoryStatusSparqlEndpoints(@PathVariable @NotNull Integer days) {
        Date daysAgo = Date.from(Instant.now().minus(days, ChronoUnit.DAYS));
        return sparqlEndpointManagement.getSparqlEndpointsAfterQueryDate(daysAgo);
    }

    @GetMapping(path = "/status/{days}/{url}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public SparqlEndpoint getHistoryStatusSparqlEndpointsByUrl(@PathVariable @NotNull Integer days, @PathVariable @NotNull String url) {

        try {
            url = URLDecoder.decode(url, Charsets.UTF_8.name());
            sparqlEndpointManagement.deleteSparqlEndpointByUrl(url);
            logger.info("SparqlEndpoint deleted: {}", url);
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

        Date daysAgo = Date.from(Instant.now().minus(days, ChronoUnit.DAYS));
        try{
            return sparqlEndpointManagement.getSparqlEndpointsAfterQueryDateByUrl(daysAgo, url);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

}
