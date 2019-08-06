package it.unife.sparqlendpoint.service.thread;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
/*A bean with prototype scope will return a different instance every time
it is requested from the container. It is defined by setting the value
prototype to the @Scope annotation in the bean definition:*/
@Scope("prototype")

public class SparqlEndpointsQueryThread extends Thread {

    private final String sparqlQueryString = "SELECT * WHERE {?s ?p ?o} LIMIT 1";
    private List<String> partialSparqlEndpointsList;

    public void setPartialSparqlEndpointsList(List<String> partialSparqlEndpointsList){
        this.partialSparqlEndpointsList=partialSparqlEndpointsList;
    }

    @Override
    public void run(){}

}
