package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class SparqlEndpointDATAManagementJPATest {

    @Test
    void update() {
    }

    @Test
    void saveStatuses() {
    }

    @Test
    void getAllURLSparqlEndpoints() {
    }

    @Test
    void getURLSparqlEndpointById() {
        SparqlEndpointRepository ser = Mockito.mock(SparqlEndpointRepository.class);
        SparqlEndpointDATAManagement sedm = new SparqlEndpointDATAManagementJPAImpl(ser, null);
        Mockito.when(ser.findSparqlEndpointsById(1L))
                .thenReturn(new SparqlEndpoint.OnlySparqlEndpoint() {
                    @Override
                    public Long getId() {
                        return 1L;
                    }

                    @Override
                    public String getUrl() {
                        return null;
                    }

                    @Override
                    public String getName() {
                        return null;
                    }
                });
        SparqlEndpoint.OnlySparqlEndpoint se = sedm.getURLSparqlEndpointById(1L);

        assertEquals(1L, se.getId());
    }

    @Test
    void getAllSparqlEndpoints() {
    }

    @Test
    void getSparqlEndpointsWithCurrentStatus() {
    }

    @Test
    void getSparqlEndpointWithCurrentStatusById() {
    }

    @Test
    void getSparqlEndpointsAfterQueryDate() {
    }

    @Test
    void getSparqlEndpointsAfterQueryDateById() {
    }

    @Test
    void getCurrentlyActiveSparqlEndpoints() {
    }

    @Test
    void findFirstQueryDate() {
    }

    @Test
    void getSparqlEndpointByUrl() {
    }
}