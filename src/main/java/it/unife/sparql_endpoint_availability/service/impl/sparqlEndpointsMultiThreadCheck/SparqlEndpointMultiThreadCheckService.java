package it.unife.sparql_endpoint_availability.service.impl.sparqlEndpointsMultiThreadCheck;

import it.unife.sparql_endpoint_availability.config.AppConfig;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.service.SparqlEndpointCheckService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class SparqlEndpointMultiThreadCheckService implements SparqlEndpointCheckService {

    private final int queryNumberByThread;

    public SparqlEndpointMultiThreadCheckService(int queryNumberByThread){
        this.queryNumberByThread=queryNumberByThread;
    }

    @Override
    public List<SparqlEndpointStatus> executeCheck(List<SparqlEndpoint> sparqlEndpoints) {

        /*MultiThread Query*/
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        List<SparqlEndpointsCheckThread> threads = new ArrayList<>();


        for (int i = 0; i < sparqlEndpoints.size(); i = i + queryNumberByThread) {

            SparqlEndpointsCheckThread thread = (SparqlEndpointsCheckThread) context.getBean("sparqlEndpointsCheckThread");
            threads.add(thread);
            thread.setPartialSparqlEndpointsList(sparqlEndpoints.subList(i, Math.min(i + queryNumberByThread, sparqlEndpoints.size())));
            thread.start();
        }

        List<SparqlEndpointStatus> statusList = new ArrayList<>();

        try {
            for (SparqlEndpointsCheckThread thread : threads) {
                thread.join();
                statusList.addAll(thread.getSparqlEndpointStatusList());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return statusList;
    }
}
