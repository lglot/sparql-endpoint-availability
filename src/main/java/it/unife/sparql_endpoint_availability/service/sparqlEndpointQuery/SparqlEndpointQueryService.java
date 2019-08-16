package it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;


import java.util.List;

public interface SparqlEndpointQueryService {

    List<SparqlEndpointStatus> executeQuery(List<SparqlEndpoint> sparqlEndpoints);
}
