package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
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
    //@Transactional
    public List<SparqlEndpoint> saveAndGet(List<String> sparqlUrlList) {

        List<SparqlEndpoint> sparqlEndpointList = new ArrayList<>();

        for (String serviceURL : sparqlUrlList) {
            if (!sparqlEndpointRepository.existsSparqlEndpointByServiceURL(serviceURL)) {
                SparqlEndpoint sparqlEndpoint = new SparqlEndpoint();
                sparqlEndpoint.setServiceURL(serviceURL);
                sparqlEndpointList.add(sparqlEndpoint);
            }
        }
        if (sparqlEndpointList.size() > 0)
            sparqlEndpointRepository.saveAll(sparqlEndpointList);

        return (List<SparqlEndpoint>) sparqlEndpointRepository.findAll();
    }

    @Override
    //@Transactional
    public void saveStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses) {
        sparqlEndpointStatusRepository.saveAll(sparqlEndpointStatuses);
    }

    @Override
    //@Transactional
    public List<SparqlEndpoint> getAllSparqlStatues() {
        return (List<SparqlEndpoint>) sparqlEndpointRepository.findAll();
    }

    @Override
    //@Transactional
    public List<SparqlEndpointStatus> getCurrentSparqlStatuses() {
        return null;
    }
}
