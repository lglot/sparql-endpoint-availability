package it.unife.sparql_endpoint_availability.service;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;

import java.util.List;

public interface SparqlEndpointCheckService {

    List<SparqlEndpointStatus> executeCheck(List<SparqlEndpoint> sparqlEndpoints);

}
