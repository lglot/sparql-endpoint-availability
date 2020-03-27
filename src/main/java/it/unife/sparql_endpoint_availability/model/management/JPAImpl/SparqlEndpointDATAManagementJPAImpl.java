package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class SparqlEndpointDATAManagementJPAImpl implements SparqlEndpointDATAManagement {


    private SparqlEndpointRepository sparqlEndpointRepository;
    private SparqlEndpointStatusRepository sparqlEndpointStatusRepository;

    @Autowired
    public SparqlEndpointDATAManagementJPAImpl(SparqlEndpointRepository sparqlEndpointRepository,
                                               SparqlEndpointStatusRepository sparqlEndpointStatusRepository) {
        this.sparqlEndpointRepository = sparqlEndpointRepository;
        this.sparqlEndpointStatusRepository = sparqlEndpointStatusRepository;
    }


    @Override
    @Transactional
    /*Metodo per aggiornare la lista degli sparql Endpoint sul DB,in input il metodo riceve
    * la lista degli URL degli endpoint letti da file. Nello specifico verrannò aggiunti al DB gli endpoint trovati nella lista-file che
    * non sono nella lista-DB, e viceversa verranno cancellati dal DB, gli endpoint della lista-DB che non sono presenti nella lista-file */
    public void update(List<String> sparqlUrlListResource) {

        List<SparqlEndpoint> sparqlListDB = sparqlEndpointRepository.findAllByOrderById();

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
    @Transactional(readOnly = true)
    public List<SparqlEndpoint> getAllSparqlEndpoints() {
        return sparqlEndpointRepository.findAllByOrderById();
    }

    /*GET sparql URL with STATUS LIST*/

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint> getSparqlEndpointsWithCurrentStatus() {

        return sparqlEndpointRepository.findAllWithLastStatus();
    }

    @Override
    @Transactional(readOnly = true)
    public SparqlEndpoint getSparqlEndpointWithCurrentStatusById(Long id) {
        return sparqlEndpointRepository.findByIdWithLastStatus(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint> getSparqlEndpointsAfterQueryDate(Date queryDate) {
        return sparqlEndpointRepository.findAllAfterQueryDateStatus(queryDate);
    }

    @Override
    @Transactional(readOnly = true)
    public SparqlEndpoint getSparqlEndpointsAfterQueryDateById(Date queryDate,Long id){
        return sparqlEndpointRepository.findByIdAfterQueryDateStatus(queryDate,id);
    }

    @Override
    @Transactional(readOnly = true)
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
