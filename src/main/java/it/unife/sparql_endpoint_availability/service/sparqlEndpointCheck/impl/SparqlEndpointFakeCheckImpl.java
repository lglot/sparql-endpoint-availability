package it.unife.sparql_endpoint_availability.service.sparqlEndpointCheck.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointCheck.SparqlEndpointCheckService;

public class SparqlEndpointFakeCheckImpl implements SparqlEndpointCheckService {

    @Override
    public List<SparqlEndpointStatus> executeCheck(List<SparqlEndpoint> sparqlEndpoints) {

        List<SparqlEndpointStatus> sparqlEndpointStatusList = new ArrayList<>();

        for (SparqlEndpoint se : sparqlEndpoints) {

            SparqlEndpointStatus seStatus = new SparqlEndpointStatus();
            seStatus.setSparqlEndpoint(se);
            Random rd = new Random();
            seStatus.setActive(rd.nextBoolean());
            seStatus.setQueryDate(new Timestamp(System.currentTimeMillis()));
            sparqlEndpointStatusList.add(seStatus);
        }

        return sparqlEndpointStatusList;
    }

}