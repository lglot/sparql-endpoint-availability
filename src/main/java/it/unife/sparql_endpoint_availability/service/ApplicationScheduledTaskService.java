package it.unife.sparql_endpoint_availability.service;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import it.unife.sparql_endpoint_availability.service.filereader.SparqlFileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Service
@Scope("singleton")
class ApplicationScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationScheduledTaskService.class);
    private int iterator;

    private final SparqlEndpointManagement sparqlEndpointManagement;
    private final SparqlFileReader sparqlFileReader;
    private final it.unife.sparql_endpoint_availability.service.sparqlEndpointCheck.SparqlEndpointCheckService sparqlEndpointCheckService;
    private final SparqlEndpointStatusRepository sparqlEndpointStatusRepository;

    @Autowired
    public ApplicationScheduledTaskService(SparqlEndpointManagement sparqlEndpointManagement,
                                           SparqlFileReader sparqlFileReader,
                                           it.unife.sparql_endpoint_availability.service.sparqlEndpointCheck.SparqlEndpointCheckService sparqlEndpointCheckService,
                                           SparqlEndpointStatusRepository sparqlEndpointStatusRepository) {

        this.sparqlEndpointManagement = sparqlEndpointManagement;
        this.sparqlFileReader = sparqlFileReader;
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
    public synchronized void applicationTask() {

        List<SparqlEndpoint> sparqlEndpointList;

        /* Reads sparql endpoint URL from resource and save them to DATA */
        if (iterator == 1) {
            logger.info("Updating Sparql Endpoint List from Resource File");
            Set<SparqlEndpoint> sparqlEndpoints = sparqlFileReader.getSparqlEndpoints();
            sparqlEndpointManagement.saveAllIfNotExists(sparqlEndpoints);
        }

        /* Recupera gli sparql endpoint presenti sul db */
        sparqlEndpointList = sparqlEndpointManagement.getAll();

        /* Execute check and save status to DATA */
        List<SparqlEndpointStatus> sparqlEndpointStatuses = sparqlEndpointCheckService.executeCheck(sparqlEndpointList);

        sparqlEndpointManagement.saveStatuses(sparqlEndpointStatuses);
        // delete rows older than 1 year
        // calculate date
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, -1);
        Date previousYear = cal.getTime();
        sparqlEndpointStatusRepository.deleteSparqlEndpointStatusByQueryDateBefore(previousYear);

        logger.info("Executed Scheduled Check " + iterator + " terminated in date "
                + new Timestamp(System.currentTimeMillis()));

        iterator++;
    }

}
