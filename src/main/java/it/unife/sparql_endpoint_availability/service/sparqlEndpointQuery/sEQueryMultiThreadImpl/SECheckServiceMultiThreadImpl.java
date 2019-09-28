package it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.sEQueryMultiThreadImpl;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.service.config.AppConfig;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointCheckService;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.sEQueryMultiThreadImpl.thread.SparqlEndpointsCheckThread;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class SECheckServiceMultiThreadImpl implements SparqlEndpointCheckService {

    private final int queryNumberByThread;

    public SECheckServiceMultiThreadImpl(int queryNumberByThread){
        this.queryNumberByThread=queryNumberByThread;
    }

    @Override
    public List<SparqlEndpointStatus> executeCheck(List<SparqlEndpoint> sparqlEndpoints) {

        /*MultiThread Query*/
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        List<SparqlEndpointsCheckThread> threads = new ArrayList<>();


        for (int i = 0; i < sparqlEndpoints.size(); i = i + queryNumberByThread) {
            //SparqlEndpointsCheckThread thread = (SparqlEndpointsCheckThread) context.getBean("sparqlEndpointsCheckThread");
            SparqlEndpointsCheckThread thread = new SparqlEndpointsCheckThread();
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
