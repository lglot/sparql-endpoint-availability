package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SparqlEndpointManagementJPAImpl implements SparqlEndpointManagement {


    private SparqlEndpointRepository sparqlEndpointRepository;
    private SparqlEndpointStatusRepository sparqlEndpointStatusRepository;

    @Autowired
    public SparqlEndpointManagementJPAImpl(SparqlEndpointRepository sparqlEndpointRepository,
                                           SparqlEndpointStatusRepository sparqlEndpointStatusRepository){
        this.sparqlEndpointRepository=sparqlEndpointRepository;
        this.sparqlEndpointStatusRepository=sparqlEndpointStatusRepository;
    }


    @Override
    @Transactional
    public List<SparqlEndpoint> saveAndGet(List<String> sparqlUrlList) {

        List<SparqlEndpoint> sparqlEndpointListToSave = new ArrayList<>();

        for (String serviceURL : sparqlUrlList) {
            if (!sparqlEndpointRepository.existsSparqlEndpointByServiceURL(serviceURL)) {
                SparqlEndpoint sparqlEndpoint = new SparqlEndpoint();
                sparqlEndpoint.setServiceURL(serviceURL);
                sparqlEndpointListToSave.add(sparqlEndpoint);
            }
        }
        if (sparqlEndpointListToSave.size() > 0)
            sparqlEndpointRepository.saveAll(sparqlEndpointListToSave);


        return (List<SparqlEndpoint>) sparqlEndpointRepository.findAll();
    }

    @Override
    @Transactional
    public void saveStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses) {
        sparqlEndpointStatusRepository.saveAll(sparqlEndpointStatuses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint.OnlyURL> getAllSE() {
        return sparqlEndpointRepository.findAllURL();
    }

    @Override
    @Transactional(readOnly = true)
    public SparqlEndpoint.OnlyURL getSEById(Long id) {
        return sparqlEndpointRepository.findSparqlEndpointsById(id);
    }

    @Override
    public List<SparqlEndpoint> getAllSEWithStatus() {
        return (List<SparqlEndpoint>) sparqlEndpointRepository.findAll();
    }

    /*GET sparql URL with STATUS LIST*/

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint> getSEWithCurrentStatus() {

       return sparqlEndpointRepository.findAllWithLastStatus();
    }

    @Override
    public SparqlEndpoint getSEWithCurrentStatusById(Long id) {
        return sparqlEndpointRepository.findByIdWithLastStatus(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint> getSEWithStatusAfterQueryDate(Date queryDate) {
        return sparqlEndpointRepository.findAllAfterQueryDateStatus(queryDate);
    }

    @Override
    public List<SparqlEndpoint> getCurrentlyActiveSE() {
        return sparqlEndpointRepository.findOnlyCurrentlyActive();
    }




    /*GET only Status*/

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpointStatus> getAllSparqlStatus() {
        return (List<SparqlEndpointStatus>) sparqlEndpointStatusRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpointStatus> getCurrentSparqlStatus() {
        return sparqlEndpointStatusRepository.findLastSparqlEnpointStatus();

    }

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpointStatus> getSparqlStatusAfterQueryDate(Date queryDate) {
        return sparqlEndpointStatusRepository.findSparqlEndpointStatusByQueryDateAfter(queryDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Date findFirstQueryDate() {

        SparqlEndpointStatus s = sparqlEndpointStatusRepository.findTopByOrderByQueryDate();
        return s.getQueryDate();
    }
}
