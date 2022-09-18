package it.unife.sparql_endpoint_availability.service.impl.sparqlEndpointsMultiThreadCheck;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import org.apache.jena.query.QueryExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
/*
 * A bean with prototype scope will return a different instance every time
 * it is requested from the container. It is defined by setting the value
 * prototype to the @Scope annotation in the bean definition:
 */
@Scope("prototype")
public class SparqlEndpointsCheckThread extends Thread {

    private List<SparqlEndpoint> partialSparqlEndpointsList;
    private final List<SparqlEndpointStatus> sparqlEndpointStatusList;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointsCheckThread.class);

    public void setPartialSparqlEndpointsList(List<SparqlEndpoint> partialSparqlEndpointsList) {
        this.partialSparqlEndpointsList = partialSparqlEndpointsList;
    }

    public List<SparqlEndpointStatus> getSparqlEndpointStatusList() {
        return sparqlEndpointStatusList;
    }

    public SparqlEndpointsCheckThread() {
        super();
        sparqlEndpointStatusList = new ArrayList<>();
    }

    @Override
    public void run() {

        for (SparqlEndpoint sparqlEndpoint : partialSparqlEndpointsList) {

            SparqlEndpointStatus status = new SparqlEndpointStatus();
            status.setSparqlEndpoint(sparqlEndpoint);

            // String sparqlQueryString = "SELECT * WHERE {?s ?p ?o} LIMIT 1";
            String sparqlQueryString = "ASK {?s ?p ?o}";
            /*
             * QueryExecution -> A interface for a single execution of a query.
             * QueryExecutionFactory -> Place to make QueryExecution objects from Query
             * objects or a string.
             * .sparqlService(String service, Query query) -> Create a QueryExecution that
             * will access a SPARQL service over HTTP
             */
            QueryExecution qexec = QueryExecution.service(sparqlEndpoint.getUrl(), sparqlQueryString);

            ExecutorService executor = Executors.newCachedThreadPool();

            //interfaccia funzionale
            Callable<Boolean> task = qexec::execAsk;

            Future<Boolean> future = executor.submit(task);
            try {
                logger.debug("Send query to: " + sparqlEndpoint.getUrl());
                status.setActive(future.get(10, TimeUnit.SECONDS));
            } catch (TimeoutException ex) {
                future.cancel(true);
                status.setActive(false);
            } catch (InterruptedException e) {
                logger.info("Query execution interrupted");
                status.setActive(false);
                future.cancel(true);
            } catch (ExecutionException e) {
                status.setActive(false);
            }
            // else status.setActive(true);
            status.setQueryDate(new Timestamp(System.currentTimeMillis()));
            sparqlEndpointStatusList.add(status);
        }

    }
}
