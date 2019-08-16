package it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.sEQueryMultiThreadImpl;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.service.config.AppConfig;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointQueryService;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.sEQueryMultiThreadImpl.thread.SparqlEndpointsQueryThread;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class SEQueryServiceMultiThreadImpl implements SparqlEndpointQueryService {

    private final int queryNumberByThread;

    public SEQueryServiceMultiThreadImpl(int queryNumberByThread){
        this.queryNumberByThread=queryNumberByThread;
    }

    @Override
    public List<SparqlEndpointStatus> executeQuery(List<SparqlEndpoint> sparqlEndpoints) {

        /*MultiThread Query*/
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        List<SparqlEndpointsQueryThread> threads = new ArrayList<>();


        for (int i = 0; i < sparqlEndpoints.size(); i = i + queryNumberByThread) {
            SparqlEndpointsQueryThread thread = (SparqlEndpointsQueryThread) context.getBean("sparqlEndpointsQueryThread");
            threads.add(thread);
            thread.setPartialSparqlEndpointsList(sparqlEndpoints.subList(i, Math.min(i + queryNumberByThread, sparqlEndpoints.size())));
            thread.start();
        }

        List<SparqlEndpointStatus> statusList = new ArrayList<>();

        try {
            for (SparqlEndpointsQueryThread thread : threads) {
                thread.join();
                statusList.addAll(thread.getSparqlEndpointStatusList());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return statusList;
    }
}
