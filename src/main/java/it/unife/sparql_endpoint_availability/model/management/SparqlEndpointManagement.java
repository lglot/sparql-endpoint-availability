package it.unife.sparql_endpoint_availability.model.management;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;

import java.util.Date;
import java.util.List;

public interface SparqlEndpointManagement {

    List<SparqlEndpoint> saveAndGet(List<String> sparqlUrlList);

    void saveStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses);

    List<SparqlEndpoint.OnlyURL> getAllSE();

    SparqlEndpoint.OnlyURL getSEById(Long id);

    List<SparqlEndpoint> getAllSEWithStatus();

    List<SparqlEndpoint> getSEWithCurrentStatus();

    SparqlEndpoint getSEWithCurrentStatusById(Long id);

    List<SparqlEndpoint> getSEWithStatusAfterQueryDate(Date queryDate);

    List<SparqlEndpoint> getCurrentlyActiveSE();

    /*STATUS*/

    List<SparqlEndpointStatus> getAllSparqlStatus();

    List<SparqlEndpointStatus> getCurrentSparqlStatus();

    List<SparqlEndpointStatus> getSparqlStatusAfterQueryDate(Date queryDate);

    Date findFirstQueryDate();

}
