package it.unife.sparql_endpoint_availability.model.management;

import it.unife.sparql_endpoint_availability.exception.SparqlEndpointAlreadyExistsException;
import it.unife.sparql_endpoint_availability.exception.SparqlEndpointNotFoundException;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;

import java.util.Date;
import java.util.List;
import java.util.Set;

//Componente che gestisce l'accesso alla persistenza e quindi al databasse
public interface SparqlEndpointManagement {

    /*
     * Metodo per aggiornare la lista degli sparql Endpoint sul DB,in input il
     * metodo riceve
     * l'insieme degli endpoint letti da file.
     */
    void saveAllIfNotExists(Set<SparqlEndpoint> sparqlEndpoints);

    /*
     * Metodo per aggiornare gli stati degli sparql enpoint sul DB ottenuti dai
     * risulati delle query sparql
     */
    void saveStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses);


    /*
     * Ottiene uno sparql endpoint in base all'id specificato (ritorna solo l'URL)
     */
    SparqlEndpoint getById(Long id) throws SparqlEndpointNotFoundException;

    /**
     * Return the SPARQL endpoint identified by the URL
     * 
     * @param url the URL of the SPARQL endpoint
     * @return the SPARQL endpoint identified by the URL
     */
    SparqlEndpoint getSparqlEndpointByUrl(String url) throws SparqlEndpointNotFoundException;

    /*
     * Ottiene la lista degli sparql endpoint
     */
    List<SparqlEndpoint> getAll();

    /*
     * Ottiene la lista di tutti gli sparql endpoint con lo stato
     * attuale (risultato dell'ultima
     * query sparql eseguita)
     */
    List<SparqlEndpoint> getSparqlEndpointsWithCurrentStatus();

    /* ottiene uno sparql enpoint con lo stato attuale in base all'id specificato */
    SparqlEndpoint getSparqlEndpointWithCurrentStatusById(Long id) throws SparqlEndpointNotFoundException;

    /*
     * recupera tutti gli sparql enpoint con i risultati delle query sparql eseguite
     * dopo la data specificata
     */
    List<SparqlEndpoint> getSparqlEndpointsAfterQueryDate(Date queryDate);

    /*
     * recupera uno sparql enpoint in base all'id specificato e con i risultati
     * delle query sparql eseguite dopo
     * la data specificata
     */
    SparqlEndpoint getSparqlEndpointsAfterQueryDateById(Date queryDate, Long id) throws SparqlEndpointNotFoundException;

    /* recupera gli sparql enpoint attualemete attivi */
    List<SparqlEndpoint> getCurrentlyActiveSparqlEndpoints();

    /* ottiene la data della prima query sparql eseguita */
    Date findFirstQueryDate();

    SparqlEndpoint createSparqlEndpoint(SparqlEndpoint se) throws SparqlEndpointAlreadyExistsException;

    SparqlEndpoint updateSparqlEndpointByUrl(String url, SparqlEndpoint sparqlEndpoint) throws SparqlEndpointNotFoundException;

    void deleteSparqlEndpointByUrl(String url) throws SparqlEndpointNotFoundException;

    public List<SparqlEndpoint> getSparqlEndpointsAfterLastWeek();
}
