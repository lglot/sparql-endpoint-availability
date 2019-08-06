package it.unife.sparqlendpoint.service.thread;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
/*A bean with prototype scope will return a different instance every time
it is requested from the container. It is defined by setting the value
prototype to the @Scope annotation in the bean definition:*/
@Scope("prototype")

public class SparqlEndpointsQueryThread extends Thread {

    private List<String> partialSparqlEndpointsList;
    private HashMap<String,Boolean> sparqlHashMap;
    private int numberActive;

    public void setPartialSparqlEndpointsList(List<String> partialSparqlEndpointsList){
        this.partialSparqlEndpointsList=partialSparqlEndpointsList;
    }

    public HashMap<String, Boolean> getSparqlHashMap(){
        return sparqlHashMap;
    }
    public int getNumberActive(){
        return numberActive;
    }

    @Override
    public void run(){

        sparqlHashMap = new HashMap<>();
        numberActive = 0;

        for (String service: partialSparqlEndpointsList) {

            String sparqlQueryString = "SELECT * WHERE {?s ?p ?o} LIMIT 1";
            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, sparqlQueryString)) {
                // qexec.setTimeout(60, TimeUnit.SECONDS);
                ResultSet rs = qexec.execSelect();
                if (rs.hasNext()) {
                    sparqlHashMap.put(service,true);
                    numberActive++;
                }
            } catch(Exception e) {
                sparqlHashMap.put(service,false);
            }
        }
    }
}
