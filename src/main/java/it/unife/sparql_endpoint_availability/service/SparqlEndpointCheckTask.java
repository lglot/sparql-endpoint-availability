package it.unife.sparql_endpoint_availability.service;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlEndpointListFileManagament;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.util.List;

@Service
@Scope("singleton")
class SparqlEndpointCheckTask {

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointCheckTask.class);
    private int iterator;

    private SparqlEndpointDATAManagement sparqlEndpointDATAManagement;
    private SparqlEndpointListFileManagament sparqlEndpointListFileManagament;
    private SparqlEndpointCheckService sparqlEndpointCheckService;

    private List<SparqlEndpoint> sparqlEndpointList;


    @Autowired
    public SparqlEndpointCheckTask(SparqlEndpointDATAManagement sparqlEndpointDATAManagement,
                                   SparqlEndpointListFileManagament sparqlEndpointListFileManagament,
                                   SparqlEndpointCheckService sparqlEndpointCheckService){

        this.sparqlEndpointDATAManagement = sparqlEndpointDATAManagement;
        this.sparqlEndpointListFileManagament = sparqlEndpointListFileManagament;
        this.sparqlEndpointCheckService = sparqlEndpointCheckService;
        sparqlEndpointList=(this.sparqlEndpointDATAManagement).getAllSparqlEndpoints();
        iterator = 1;
    }

    @Scheduled(fixedRate=1000*60*60)
    @Transactional
    public void service(){

        /*Read sparql endpoint URL from resource and save them to DATA*/
        if(sparqlEndpointListFileManagament.isModified() || iterator==1 || sparqlEndpointList.isEmpty()) {
            logger.info((iterator!=1)? "Sparql URL list Resource has been modified - " : "" + "Updating Sparql Endpoint List from Resource");
            List<String> sparqlEndpointURLlist = sparqlEndpointListFileManagament.read();
            sparqlEndpointList = sparqlEndpointDATAManagement.update(sparqlEndpointList,sparqlEndpointURLlist);
        }

        /*Execute check and save status to DATA*/
        List<SparqlEndpointStatus> sparqlEndpointStatuses = sparqlEndpointCheckService.executeCheck(sparqlEndpointList);

        sparqlEndpointDATAManagement.saveStatuses(sparqlEndpointStatuses);

        logger.info("Executed Scheduled Check "+ iterator +" terminated in date "+ new Timestamp(System.currentTimeMillis()).toString());
        iterator++;
    }
}


