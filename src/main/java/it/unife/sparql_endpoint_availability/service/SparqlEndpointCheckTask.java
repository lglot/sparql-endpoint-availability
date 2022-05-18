package it.unife.sparql_endpoint_availability.service;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointStatusRepository;
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
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlEndpointListFileManagement;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Service
@Scope("singleton")
class SparqlEndpointCheckTask {

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointCheckTask.class);
    private int iterator;

    private SparqlEndpointDATAManagement sparqlEndpointDATAManagement;
    private SparqlEndpointListFileManagement sparqlEndpointListFileManagament;
    private SparqlEndpointCheckService sparqlEndpointCheckService;
    private SparqlEndpointStatusRepository sparqlEndpointStatusRepository;

    @Autowired
    public SparqlEndpointCheckTask(SparqlEndpointDATAManagement sparqlEndpointDATAManagement,
            SparqlEndpointListFileManagement sparqlEndpointListFileManagament,
            SparqlEndpointCheckService sparqlEndpointCheckService,
            SparqlEndpointStatusRepository sparqlEndpointStatusRepository) {

        this.sparqlEndpointDATAManagement = sparqlEndpointDATAManagement;
        this.sparqlEndpointListFileManagament = sparqlEndpointListFileManagament;
        this.sparqlEndpointCheckService = sparqlEndpointCheckService;
        this.sparqlEndpointStatusRepository = sparqlEndpointStatusRepository;
        iterator = 1;
    }

    /*
     * servizio principale dell'applicazione eseguito una volta all'ora
     * automaticamente
     * che effettua il controllo della disponibilità degli sparql enpoint e
     * memorizza il risultato sul db
     */
    @Scheduled(fixedRate = 1000 * 60 * 60)
    @Transactional
    public synchronized void service() {

        List<SparqlEndpoint> sparqlEndpointList;

        /* Reads sparql endpoint URL from resource and save them to DATA */
        if (/* sparqlEndpointListFileManagament.isModified() || */ iterator == 1) {
            logger.info((iterator != 1) ? "Sparql URL list Resource has been modified - "
                    : "" + "Updating Sparql Endpoint List from Resource");
            Set<SparqlEndpoint> sparqlEndpoints = sparqlEndpointListFileManagament.getSparqlEndpoints();
            sparqlEndpointDATAManagement.update(sparqlEndpoints);
        }

        /* Recupera gli sparql endpoint presenti sul db */
        sparqlEndpointList = sparqlEndpointDATAManagement.getAllSparqlEndpoints();

        /* Execute check and save status to DATA */
        List<SparqlEndpointStatus> sparqlEndpointStatuses = sparqlEndpointCheckService.executeCheck(sparqlEndpointList);

        sparqlEndpointDATAManagement.saveStatuses(sparqlEndpointStatuses);
        // delete rows older than 1 year
        // calculate date
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, -1);
        Date previousYear = cal.getTime();
        sparqlEndpointStatusRepository.deleteSparqlEndpointStatusByQueryDateBefore(previousYear);

        logger.info("Executed Scheduled Check " + iterator + " terminated in date "
                + new Timestamp(System.currentTimeMillis()).toString());

        iterator++;
    }

}
