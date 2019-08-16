package it.unife.sparql_endpoint_availability;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlListResource;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.List;

import static java.lang.Thread.sleep;

@Service
@Scope("singleton")
class SparqlEndpointCheckTask {

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointCheckTask.class);
    private int iterator;

    private SparqlEndpointManagement sparqlEndpointManagement;
    private SparqlListResource sparqlListResource;
    private SparqlEndpointQueryService sparqlEndpointQueryService;

    private List<SparqlEndpoint> sparqlEndpointList;


    @Autowired
    public SparqlEndpointCheckTask(SparqlEndpointManagement sparqlEndpointManagement,
                                   SparqlListResource sparqlListResource,
                                   SparqlEndpointQueryService sparqlEndpointQueryService){

        this.sparqlEndpointManagement=sparqlEndpointManagement;
        this.sparqlListResource=sparqlListResource;
        this.sparqlEndpointQueryService=sparqlEndpointQueryService;

        iterator = 1;
    }

    @Scheduled(fixedRate=1000*60*60)
    @Transactional
    public void service(){
        /*Read spaql endpoint URL from resource and save them to DATA*/

        if(sparqlListResource.isModified() || iterator==1) {
            logger.info("Sparql URL list Resource has been modified\nUpdating Sparql Endpoint List from Resource");
            List<String> sparqlEndpointURLlist = sparqlListResource.read();
            sparqlEndpointList = sparqlEndpointManagement.saveAndGet(sparqlEndpointURLlist);
        }

        /*Execute query and save status to DATA*/
        List<SparqlEndpointStatus> sparqlEndpointStatuses = sparqlEndpointQueryService.executeQuery(sparqlEndpointList);

        sparqlEndpointManagement.saveStatuses(sparqlEndpointStatuses);

        logger.info("Executed Scheduled Check "+ iterator +" in date "+ new Timestamp(System.currentTimeMillis()).toString());
        iterator++;

    }
}


