package it.unife.sparql_endpoint_availability.model.management;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;

import java.util.Date;
import java.util.List;

//Componente che gestisce l'accesso alla persistenza e quindi al databasse
public interface SparqlEndpointDATAManagement {

    /* Metodo per aggiornare la lista degli sparql Endpoint sul DB,in input il metodo riceve
    * la lista degli URL degli endpoint letti da file.*/
    void update(List<String> sparqlUrlList);

    /*Metodo per aggiornare gli stati degli sparql enpoint sul DB ottenuti dai risulati delle query sparql*/
    void saveStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses);

    //metodo per ottenere tutti gli sparql endpoint memorizzati sul DB (solo URL)
    List<SparqlEndpoint.OnlyURL> getAllURLSparqlEndpoints();

    //ottiene uno sparql endpoint in base all'id specificato (ritorna solo l'URL)
    SparqlEndpoint.OnlyURL getURLSparqlEndpointById(Long id);

    //ottiene la lista degli sparql enpoint presenti sul db
    List<SparqlEndpoint> getAllSparqlEndpoints();

    /*ottiene la lista di tutti gli sparql enpoint presenti sul db con lo stato attuale (risultato dell'ultima
    * query sparql eseguita*/
    List<SparqlEndpoint> getSparqlEndpointsWithCurrentStatus();

    /* ottiene uno sparql enpoint con lo stato attuale in base all'id specificato*/
    SparqlEndpoint getSparqlEndpointWithCurrentStatusById(Long id);

    /*recupera tutti gli sparql enpoint con i risultati delle query sparql eseguite dopo la data specificata*/
    List<SparqlEndpoint> getSparqlEndpointsAfterQueryDate(Date queryDate);

    /*recupera uno sparql enpoint in base all'id specificato e con i risultati delle query sparql eseguite dopo
    * la data specificata */
    SparqlEndpoint getSparqlEndpointsAfterQueryDateById(Date queryDate,Long id);

    /*recupera gli sparql enpoint attualemete attivi*/
    List<SparqlEndpoint> getCurrentlyActiveSparqlEndpoints();

    /*ottiene la data della prima query sparql eseguita*/
    Date findFirstQueryDate();

}
