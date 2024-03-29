package it.unife.sparql_endpoint_availability.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import it.unife.sparql_endpoint_availability.dto.SparqlEndpointDTO;
import it.unife.sparql_endpoint_availability.dto.SparqlEndpointRequest;
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
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/api/endpoints", produces = "application/json")
@OpenAPIDefinition(info = @Info(title = "Sparql Endpoint Availability API", version = "0.1",
        description = "This is the API documentation for the Sparql Endpoint Availability project" +
                " developed by the University of Ferrara." +
                "\n\n"+
                "__Authenthication__ \n\n" +
                "To use this API you need to be authenticated. To do so, you can get the token by visiting the dashboard at */user*" +
                " and then use it in the Authorization header of your requests." +
                "\n\n"+
                "Alternatively, you need to send a POST request to the /api/login endpoint with the following body:" +
                "\n\n"+
                "```json\n" +
                "{\n" +
                "  \"username\": \"your_username\",\n" +
                "  \"password\": \"your_password\"\n" +
                "}\n" +
                "```\n\n" +
                "The response will be a JSON object containing the token that you need to use in the Authorization header of every request to the API."))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Bearer authentication")
@SecurityRequirement(name = "bearerAuth")
public class SparqlEndpointAvailabilityRestController {

    private final SparqlEndpointManagement sparqlEndpointManagement;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointAvailabilityController.class);

    @Autowired
    public SparqlEndpointAvailabilityRestController(SparqlEndpointManagement sparqlEndpointManagement) {
        this.sparqlEndpointManagement = sparqlEndpointManagement;
    }

    @Operation(summary = "Get all the SPARQL endpoints")
    @GetMapping(path = "")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Iterable<SparqlEndpointDTO> getAllSparqlEndpoints() {
        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointManagement.getSparqlEndpointsWithCurrentStatus();
        return SparqlEndpointDTO.fromSparqlEndpointList(sparqlEndpointList);
    }

    @Operation(summary = "Get a SPARQL endpoint by its ID - ONLY ADMIN")
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

    @Operation(summary = "Get a SPARQL endpoint by its URL")
    @GetMapping(path = "/url")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public SparqlEndpointDTO getSparqlEndpointByUrl(@RequestParam @NotNull String url) {
        try {
            url = URLDecoder.decode(url, Charsets.UTF_8);
            SparqlEndpoint se = sparqlEndpointManagement.getSparqlEndpointByUrl(url);
            return SparqlEndpointDTO.fromSparqlEndpoint(se);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SparqlEndpoint not found");
        }
    }
    @Operation(summary = "Create a new SPARQL endpoint - ONLY ADMIN")
    @PostMapping(path = "")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SparqlEndpointDTO> createSparqlEndpoint(@RequestBody @NotNull SparqlEndpointRequest sparqlEndpoint) {
        try{
            SparqlEndpoint se = SparqlEndpointRequest.toSparqlEndpoint(sparqlEndpoint);
            sparqlEndpointManagement.createSparqlEndpoint(se);
            logger.info("SparqlEndpoint created: {}", se);
            return new ResponseEntity<>(SparqlEndpointDTO.fromSparqlEndpoint(se), HttpStatus.CREATED);
        } catch (SparqlEndpointAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SparqlEndpoint already exists", e);
        }
    }
    @Operation(summary = "Update a SPARQL endpoint - ONLY ADMIN")
    @PutMapping(path = "/url")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public SparqlEndpointDTO updateSparqlEndpointByUrl(@RequestParam @NotNull String url, @RequestBody @NotNull SparqlEndpointRequest sparqlEndpoint) {
        try {
            url = URLDecoder.decode(url, Charsets.UTF_8);
            SparqlEndpoint se = SparqlEndpointRequest.toSparqlEndpoint(sparqlEndpoint);
            SparqlEndpoint result = sparqlEndpointManagement.updateSparqlEndpointByUrl(url, se);
            logger.info("SparqlEndpoint updated: {}", result);
            return SparqlEndpointDTO.fromSparqlEndpoint(result);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SparqlEndpoint not found");

        }
    }

    @Operation(summary = "Delete a SPARQL endpoint - ONLY ADMIN")
    @DeleteMapping(path = "/url")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteSparqlEndpointByUrl(@RequestParam @NotNull String url) {
        try {
            url = URLDecoder.decode(url, Charsets.UTF_8);
            sparqlEndpointManagement.deleteSparqlEndpointByUrl(url);
            logger.info("SparqlEndpoint deleted: {}", url);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SparqlEndpoint not found");
        }
    }


    @Operation(summary = "Get the current status of all SPARQL endpoints")
    @GetMapping(path = "/status/current")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Iterable<SparqlEndpoint> getSparqlEndpointsWithCurrentStatus() {
        return sparqlEndpointManagement.getSparqlEndpointsWithCurrentStatus();
    }

    @Operation(summary = "Get the current active SPARQL endpoints")
    @GetMapping(path = "/status/current/active")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Iterable<SparqlEndpointDTO> getCurrentlyActiveSparqlEndpoints() {
        return SparqlEndpointDTO.fromSparqlEndpointList(sparqlEndpointManagement.getCurrentlyActiveSparqlEndpoints());
    }

    @Operation(summary = "Get status of the specified previous days of all SPARQL endpoints")
    @GetMapping(path = "/status/{days}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Iterable<SparqlEndpoint> getHistoryStatusSparqlEndpoints(@PathVariable @NotNull Integer days) {
        LocalDateTime daysAgo = LocalDateTime.now().minusDays(days);
        return sparqlEndpointManagement.getSparqlEndpointsAfterQueryDate(daysAgo);
    }

    @Operation(summary = "Get the status of the specified prevoius days of the SPARQL endpoints specified by its URL")
    @GetMapping(path = "/status/{days}/{url}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public SparqlEndpoint getHistoryStatusSparqlEndpointsByUrl(@PathVariable @NotNull Integer days, @PathVariable @NotNull String url) {

        url = URLDecoder.decode(url, Charsets.UTF_8);
        sparqlEndpointManagement.deleteSparqlEndpointByUrl(url);
        logger.info("SparqlEndpoint deleted: {}", url);

        LocalDateTime daysAgo = LocalDateTime.now().minusDays(days);
        try{
            return sparqlEndpointManagement.getSparqlEndpointsAfterQueryDateByUrl(daysAgo, url);
        } catch (SparqlEndpointNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

}
