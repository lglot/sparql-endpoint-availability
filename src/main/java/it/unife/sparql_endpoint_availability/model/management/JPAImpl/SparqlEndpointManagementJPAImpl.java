package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import it.unife.sparql_endpoint_availability.exception.SparqlEndpointAlreadyExistsException;
import it.unife.sparql_endpoint_availability.exception.SparqlEndpointNotFoundException;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointStatusRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SparqlEndpointManagementJPAImpl implements SparqlEndpointManagement {

    private final SparqlEndpointRepository sparqlEndpointRepository;
    private final SparqlEndpointStatusRepository sparqlEndpointStatusRepository;

    public SparqlEndpointManagementJPAImpl(SparqlEndpointRepository sparqlEndpointRepository,
                                           SparqlEndpointStatusRepository sparqlEndpointStatusRepository) {
        this.sparqlEndpointRepository = sparqlEndpointRepository;
        this.sparqlEndpointStatusRepository = sparqlEndpointStatusRepository;
    }

    @Override
    /*
     * @return void
     * salva una lista di endpoint nella base dati se non esistono già nella base dati
     */
    public void saveAllIfNotExists(Set<SparqlEndpoint> sparqlEndpoints) {

        List<SparqlEndpoint> sparqlListDB = sparqlEndpointRepository.findAllByOrderById();

        // Hashset -> a collection that contains no duplicate elements.
        Set<SparqlEndpoint> sparqlEndpointsDB = new HashSet<>(sparqlListDB);

        // set of SPARQL endpoints to add to DB
        Set<SparqlEndpoint> sparqlEndpointsToAdd = new HashSet<>(sparqlEndpoints);
        sparqlEndpointsToAdd.removeAll(sparqlEndpointsDB);

        if (sparqlEndpointsToAdd.size() > 0) {
            sparqlEndpointRepository.saveAll(sparqlEndpointsToAdd);
        }

    }

    @Override
    public void saveStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses) {
        sparqlEndpointStatusRepository.saveAll(sparqlEndpointStatuses);
    }


    @Override
    public SparqlEndpoint getById(Long id) throws SparqlEndpointNotFoundException {
        Optional<SparqlEndpoint> se = sparqlEndpointRepository.findById(id);
        if (!se.isPresent()) {
            throw new SparqlEndpointNotFoundException(id);
        }
        return se.get();
    }

    @Override
    public List<SparqlEndpoint> getAll() {
        return sparqlEndpointRepository.findAllByOrderById();
    }

    /* GET sparql URL with STATUS LIST */
    @Override
    public List<SparqlEndpoint> getSparqlEndpointsWithCurrentStatus() {
        return sparqlEndpointRepository.findAllWithCurrentStatus();
    }

    @Override
    public SparqlEndpoint getSparqlEndpointWithCurrentStatusById(Long id) throws SparqlEndpointNotFoundException {
        Optional<SparqlEndpoint> se = Optional.ofNullable(sparqlEndpointRepository.findByIdWithCurrentStatus(id));
        if (!se.isPresent()) {
            throw new SparqlEndpointNotFoundException(id);
        }
        return se.get();
    }

    @Override
    public List<SparqlEndpoint> getSparqlEndpointsAfterQueryDate(Date queryDate) {
        return sparqlEndpointRepository.findAllAfterQueryDateStatus(queryDate);
    }

    @Override
    public SparqlEndpoint getSparqlEndpointsAfterQueryDateById(Date queryDate, Long id) throws SparqlEndpointNotFoundException {
        Optional<SparqlEndpoint> se = Optional.ofNullable(sparqlEndpointRepository.findByIdAfterQueryDateStatus(queryDate, id));
        if (!se.isPresent()) {
            throw new SparqlEndpointNotFoundException(id);
        }
        return se.get();
    }

    @Override
    public List<SparqlEndpoint> getCurrentlyActiveSparqlEndpoints() {
        return sparqlEndpointRepository.findOnlyCurrentlyActive();
    }

    @Override
    public Date findFirstQueryDate() {
        SparqlEndpointStatus s = sparqlEndpointStatusRepository.findTopByOrderByQueryDate();
        return s.getQueryDate();
    }

    @Override
    public SparqlEndpoint createSparqlEndpoint(SparqlEndpoint se) throws SparqlEndpointAlreadyExistsException
    {
        if(sparqlEndpointRepository.existsByUrl(se.getUrl()))
            throw new SparqlEndpointAlreadyExistsException(se.getUrl());
        else
            return sparqlEndpointRepository.save(se);
    }

    /**
     * @param sparqlEndpoint the sparql endpoint to update
     * @return SparqlEndpoint the updated sparql endpoint
     * @throws SparqlEndpointNotFoundException if the sparql endpoint does not exist
     */
    @Override
    public SparqlEndpoint updateSparqlEndpointByUrl(String url, SparqlEndpoint sparqlEndpoint) throws SparqlEndpointNotFoundException {
        Optional<SparqlEndpoint> se = Optional.ofNullable(sparqlEndpointRepository.findByUrl(url));
        if (!se.isPresent()) {
            throw new SparqlEndpointNotFoundException(url);
        }
        sparqlEndpoint.setId(se.get().getId());
        return sparqlEndpointRepository.save(sparqlEndpoint);
    }

    /**
     * @param url
     * delete the sparql endpoint with the given url
     * @throws SparqlEndpointNotFoundException if the sparql endpoint does not exist
     */
    @Override
     public void deleteSparqlEndpointByUrl(String url) throws SparqlEndpointNotFoundException {
        if(sparqlEndpointRepository.existsByUrl(url)) {
            sparqlEndpointRepository.deleteByUrl(url);
        } else {
            throw new SparqlEndpointNotFoundException(url);
        }
    }

    @Override
    public SparqlEndpoint getSparqlEndpointByUrl(String url) throws SparqlEndpointNotFoundException {
        Optional<SparqlEndpoint> se = Optional.ofNullable(sparqlEndpointRepository.findByUrlWithCurrentStatus(url));
        if (!se.isPresent()) {
            throw new SparqlEndpointNotFoundException(url);
        }
        return sparqlEndpointRepository.findByUrlWithCurrentStatus(url);
    }

    //get sparql endpoint after last week

    public List<SparqlEndpoint> getSparqlEndpointsAfterLastWeek() {
        Date lastWeek = DateUtils.addDays(new Date(), -7);
        return sparqlEndpointRepository.findAllAfterQueryDateStatus(lastWeek);
    }
}
