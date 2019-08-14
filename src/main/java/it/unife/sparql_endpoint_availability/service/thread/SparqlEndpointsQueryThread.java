package it.unife.sparql_endpoint_availability.service.thread;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointStatusRepository;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
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

    public List<SparqlEndpointStatus> getSparqlEndpointStatusList(){
        return sparqlEndpointStatusList;
    }

    public SparqlEndpointsQueryThread(){
        super();
        sparqlEndpointStatusList = new ArrayList<>();
    }

    @Override
    public void run() {

        //sparqlHashMap = new HashMap<>();
        //numberActive = 0;

        for (SparqlEndpoint sparqlEndpoint : partialSparqlEndpointsList) {

            SparqlEndpointStatus status = new SparqlEndpointStatus();
            status.setSparqlEndpoint(sparqlEndpoint);

            String sparqlQueryString = "SELECT * WHERE {?s ?p ?o} LIMIT 1";
            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint.getServiceURL(), sparqlQueryString)) {
                // qexec.setTimeout(60, TimeUnit.SECONDS);
                ResultSet rs = qexec.execSelect();
                if (rs.hasNext()) {
                    status.setActive(true);
                    //sparqlHashMap.put(sparqlEndpoint, true);
                    //numberActive++;
                }
            } catch (Exception e) {
                status.setActive(false);
                //sparqlHashMap.put(sparqlEndpoint, false);
            }
            status.setQueryDate(new Timestamp(System.currentTimeMillis()));
            sparqlEndpointStatusList.add(status);
        }
    }
}
