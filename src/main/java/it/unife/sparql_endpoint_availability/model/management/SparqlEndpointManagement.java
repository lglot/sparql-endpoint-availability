package it.unife.sparql_endpoint_availability.model.management;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;

import java.util.Date;
import java.util.List;

public interface SparqlEndpointManagement {

    List<SparqlEndpoint> saveAndGet(List<String> sparqlUrlList);

    void saveStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses);

    List<SparqlEndpoint.OnlyURL> getAllSparqlEndopoint();

    List<SparqlEndpoint> getSparqlWithCurrentStatus();

    List<SparqlEndpoint> getSparqlWithStatusAfterQueryDate(Date queryDate);

    /*STATUS*/

    List<SparqlEndpointStatus> getAllSparqlStatus();

    List<SparqlEndpointStatus> getCurrentSparqlStatus();

    List<SparqlEndpointStatus> getSparqlStatusAfterQueryDate(Date queryDate);

}
