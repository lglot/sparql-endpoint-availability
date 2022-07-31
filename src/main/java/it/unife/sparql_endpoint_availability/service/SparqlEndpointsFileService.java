package it.unife.sparql_endpoint_availability.service;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;

import java.util.Set;

public interface SparqlEndpointsFileService {

    Set<SparqlEndpoint> getSparqlEndpoints();
}
