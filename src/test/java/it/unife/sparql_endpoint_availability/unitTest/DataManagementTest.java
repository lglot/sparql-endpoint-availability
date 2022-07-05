package it.unife.sparql_endpoint_availability.unitTest;

import it.unife.sparql_endpoint_availability.model.management.JPAImpl.SparqlEndpointDATAManagementJPAImpl;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit4.SpringRunner;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;

public class DataManagementTest {

    @Test
    public void testGetSparqlEndpoint() {

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

}
