package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SparqlEndpointDATAManagementJPAImpl implements SparqlEndpointDATAManagement {

    private final SparqlEndpointRepository sparqlEndpointRepository;
    private final SparqlEndpointStatusRepository sparqlEndpointStatusRepository;

    public SparqlEndpointDATAManagementJPAImpl(SparqlEndpointRepository sparqlEndpointRepository,
            SparqlEndpointStatusRepository sparqlEndpointStatusRepository) {
        this.sparqlEndpointRepository = sparqlEndpointRepository;
        this.sparqlEndpointStatusRepository = sparqlEndpointStatusRepository;
    }

    @Override
    @Transactional
    /*
     * Metodo per aggiornare la lista degli sparql Endpoint sul DB,in input il
     * metodo riceve
     * la lista degli URL degli endpoint letti da file. Nello specifico verrannò
     * aggiunti al DB gli endpoint trovati nella lista-file che
     * non sono nella lista-DB, e viceversa verranno cancellati dal DB, gli endpoint
     * della lista-DB che non sono presenti nella lista-file
     */
    public void update(Set<SparqlEndpoint> sparqlEndpoints) {

        List<SparqlEndpoint> sparqlListDB = sparqlEndpointRepository.findAllByOrderById();

        // Hashset -> a collection that contains no duplicate elements.
        Set<SparqlEndpoint> sparqlEndpointsDB = new HashSet<>(sparqlListDB);

        // set of SPARQL endpoints to remove from DB
        Set<SparqlEndpoint> sparqlEndpointsToRemove = new HashSet<>(sparqlEndpointsDB);
        sparqlEndpointsToRemove.removeAll(sparqlEndpoints);

        // set of SPARQL endpoints to add to DB
        Set<SparqlEndpoint> sparqlEndpointsToAdd = new HashSet<>(sparqlEndpoints);
        sparqlEndpointsToAdd.removeAll(sparqlEndpointsDB);

        //sparqlEndpointsToRemove.removeAll(Collections.singleton(null));

        if (sparqlEndpointsToRemove.size() > 0) {
            List<String> urls = sparqlEndpointsToRemove
                    .stream()
                    .map(SparqlEndpoint::getUrl)
                    .collect(Collectors.toList());
            for (String url : urls) {
                sparqlEndpointRepository.deleteByUrl(url.trim());
            }
        }

        if (sparqlEndpointsToAdd.size() > 0) {
            sparqlEndpointRepository.saveAll(sparqlEndpointsToAdd);
        }

    }

    @Override
    @Transactional
    public void saveStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses) {
        sparqlEndpointStatusRepository.saveAll(sparqlEndpointStatuses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint.OnlySparqlEndpoint> getAllURLSparqlEndpoints() {
        return sparqlEndpointRepository.findAllURL();
    }

    @Override
    @Transactional(readOnly = true)
    public SparqlEndpoint.OnlySparqlEndpoint getURLSparqlEndpointById(Long id) {
        return sparqlEndpointRepository.findSparqlEndpointsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint> getAllSparqlEndpoints() {
        return sparqlEndpointRepository.findAllByOrderById();
    }

    /* GET sparql URL with STATUS LIST */
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
    public SparqlEndpoint getSparqlEndpointsAfterQueryDateById(Date queryDate, Long id) {
        return sparqlEndpointRepository.findByIdAfterQueryDateStatus(queryDate, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparqlEndpoint.OnlySparqlEndpoint> getCurrentlyActiveSparqlEndpoints() {
        List<SparqlEndpoint.OnlySparqlEndpoint> sparqlEnpoints = sparqlEndpointRepository.findOnlyCurrentlyActive()
                .stream()
                .map(sparqlEndpoint -> {
                    return new SparqlEndpoint.OnlySparqlEndpoint() {

                        @Override
                        public Long getId() {
                            return sparqlEndpoint.getId();
                        }

                        @Override
                        public String getUrl() {
                            return sparqlEndpoint.getUrl();
                        }

                        @Override
                        public String getName() {
                            return sparqlEndpoint.getName();
                        }

                    };
                }).collect(Collectors.toList());

        return sparqlEnpoints;
    }

    @Override
    @Transactional(readOnly = true)
    public Date findFirstQueryDate() {

        SparqlEndpointStatus s = sparqlEndpointStatusRepository.findTopByOrderByQueryDate();
        return s.getQueryDate();
    }

    @Override
    public SparqlEndpoint getSparqlEndpointByUrl(String url) {
        return sparqlEndpointRepository.findByUrl(url);
    }
}
