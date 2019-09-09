package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SparqlEndpointManagementJPAImpl implements SparqlEndpointManagement {


    private SparqlEndpointRepository sparqlEndpointRepository;
    private SparqlEndpointStatusRepository sparqlEndpointStatusRepository;

    @Autowired
    public SparqlEndpointManagementJPAImpl(SparqlEndpointRepository sparqlEndpointRepository,
                                           SparqlEndpointStatusRepository sparqlEndpointStatusRepository) {
        this.sparqlEndpointRepository = sparqlEndpointRepository;
        this.sparqlEndpointStatusRepository = sparqlEndpointStatusRepository;
    }


    @Override
    @Transactional
    /*Metodo per aggiornare la lista degli sparql Endpoint sul DB,in input il metodo riceve la lista presente attualmente sul DB
    * e la lista degli URL degli endpoint letti da file. Nello specifico verrannò aggiunti al DB gli endpoint trovati nella lista-file che
    * non sono nella lista-DB, e viceversa verranno cancellati dal DB, gli endpoint della lista-DB che non sono presenti nella lista-file */
    public List<SparqlEndpoint> update(List<SparqlEndpoint> sparqlListDB, List<String> sparqlUrlListResource) {

        List<String> sparqlUrlListDB = sparqlListDB.stream().map(SparqlEndpoint::getServiceURL).collect(Collectors.toList());
        List<String> sparqlUrlResource1 = new ArrayList<>(sparqlUrlListResource);

        sparqlUrlListResource.removeAll(sparqlUrlListDB);
        sparqlUrlListDB.removeAll(sparqlUrlResource1);

        Set<SparqlEndpoint> sparqlEndpointSetToSave = sparqlUrlListResource.stream().map(serviceURL -> {
            SparqlEndpoint sparqlEndpoint = new SparqlEndpoint();
            sparqlEndpoint.setServiceURL(serviceURL);
            return sparqlEndpoint;
        }).collect(Collectors.toSet());

        if (sparqlEndpointSetToSave.size() > 0)
            sparqlEndpointRepository.saveAll(sparqlEndpointSetToSave);

        if(sparqlUrlListDB.size()>0)
            sparqlEndpointRepository.deleteByServiceURLIn(sparqlUrlListDB);

        return (List<SparqlEndpoint>) sparqlEndpointRepository.findAll();
    }

    @Override
    @Transactional
    public void saveStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses) {
        sparqlEndpointStatusRepository.saveAll(sparqlEndpointStatuses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint.OnlyURL> getAllURLSparqlEndpoints() {
        return sparqlEndpointRepository.findAllURL();
    }

    @Override
    @Transactional(readOnly = true)
    public SparqlEndpoint.OnlyURL getURLSparqlEndpointById(Long id) {
        return sparqlEndpointRepository.findSparqlEndpointsById(id);
    }

    @Override
    public List<SparqlEndpoint> getAllSparqlEndpoints() {
        return (List<SparqlEndpoint>) sparqlEndpointRepository.findAll();
    }

    /*GET sparql URL with STATUS LIST*/

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint> getSparqlEndpointsWithCurrentStatus() {

        return sparqlEndpointRepository.findAllWithLastStatus();
    }

    @Override
    public SparqlEndpoint getSparqlEndpointWithCurrentStatusById(Long id) {
        return sparqlEndpointRepository.findByIdWithLastStatus(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint> getSparqlEndpointsAfterQueryDate(Date queryDate) {
        return sparqlEndpointRepository.findAllAfterQueryDateStatus(queryDate);
    }

    @Override
    public List<SparqlEndpoint> getCurrentlyActiveSparqlEndpoints() {
        return sparqlEndpointRepository.findOnlyCurrentlyActive();
    }

    @Override
    @Transactional(readOnly = true)
    public Date findFirstQueryDate() {

        SparqlEndpointStatus s = sparqlEndpointStatusRepository.findTopByOrderByQueryDate();
        return s.getQueryDate();
    }
}
