package it.unife.sparql_endpoint_availability.service.filereader;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;

import java.util.Set;

public interface SparqlFileReader {

    Set<SparqlEndpoint> getSparqlEndpoints();
}
