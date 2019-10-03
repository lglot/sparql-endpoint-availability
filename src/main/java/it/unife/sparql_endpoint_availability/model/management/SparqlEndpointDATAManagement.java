package it.unife.sparql_endpoint_availability.model.management;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;

import java.util.Date;
import java.util.List;

public interface SparqlEndpointDATAManagement {

    void update(List<String> sparqlUrlList);

    void saveStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses);

    List<SparqlEndpoint.OnlyURL> getAllURLSparqlEndpoints();

    SparqlEndpoint.OnlyURL getURLSparqlEndpointById(Long id);

    List<SparqlEndpoint> getAllSparqlEndpoints();

    List<SparqlEndpoint> getSparqlEndpointsWithCurrentStatus();

    SparqlEndpoint getSparqlEndpointWithCurrentStatusById(Long id);

    List<SparqlEndpoint> getSparqlEndpointsAfterQueryDate(Date queryDate);

    SparqlEndpoint getSparqlEndpointsAfterQueryDateById(Date queryDate,Long id);

    List<SparqlEndpoint> getCurrentlyActiveSparqlEndpoints();

    /*STATUS*/

    Date findFirstQueryDate();

}
