package it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.sEQueryMultiThreadImpl.thread;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
/*A bean with prototype scope will return a different instance every time
it is requested from the container. It is defined by setting the value
prototype to the @Scope annotation in the bean definition:*/
@Scope("prototype")

public class SparqlEndpointsQueryThread extends Thread {

    private List<SparqlEndpoint> partialSparqlEndpointsList;
    private List<SparqlEndpointStatus> sparqlEndpointStatusList;

    public void setPartialSparqlEndpointsList(List<SparqlEndpoint> partialSparqlEndpointsList) {
        this.partialSparqlEndpointsList = partialSparqlEndpointsList;
    }

    public List<SparqlEndpointStatus> getSparqlEndpointStatusList() {
        return sparqlEndpointStatusList;
    }

    public SparqlEndpointsQueryThread() {
        super();
        sparqlEndpointStatusList = new ArrayList<>();
    }

    @Override
    public void run() {

        for (SparqlEndpoint sparqlEndpoint : partialSparqlEndpointsList) {

            SparqlEndpointStatus status = new SparqlEndpointStatus();
            status.setSparqlEndpoint(sparqlEndpoint);

            String sparqlQueryString = "SELECT * WHERE {?s ?p ?o} LIMIT 1";

            /* QueryExecution -> A interface for a single execution of a query.
            * QueryExecutionFactory -> Place to make QueryExecution objects from Query objects or a string.
            * .sparqlService(String service, Query query) -> Create a QueryExecution that will access a SPARQL service over HTTP
            * */
            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint.getServiceURL(), sparqlQueryString)) {
                qexec.setTimeout(60, TimeUnit.SECONDS);
                ResultSet rs = qexec.execSelect();
                if (rs.hasNext()) {
                    status.setActive(true);
                }
                //else status.setActive(true);
            } catch (Exception e) {
                status.setActive(false);
            }
            status.setQueryDate(new Timestamp(System.currentTimeMillis()));
            sparqlEndpointStatusList.add(status);
        }

    }
}
