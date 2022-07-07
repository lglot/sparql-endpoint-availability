package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assume.assumeThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(JUnitQuickcheck.class)
class SparqlEndpointDATAManagementJPATest {

    @Test
    void updateWithEmptyDB() {

        Set<SparqlEndpoint> seInput = new HashSet<>();
        seInput.add(new SparqlEndpoint("http://localhost:8080/sparql1", "sparql1"));
        seInput.add(new SparqlEndpoint("http://localhost:8080/sparql2", "sparql2"));


        Set<SparqlEndpoint> seOutput = new HashSet<>();

        SparqlEndpointRepository ser = Mockito.mock(SparqlEndpointRepository.class);
        SparqlEndpointDATAManagement sedm = new SparqlEndpointDATAManagementJPAImpl(ser, null);

        Mockito.when(ser.findAllByOrderById()).thenReturn(new ArrayList<>());
        Mockito.doNothing().when(ser).deleteByUrl(anyString());

        // copy seInput list to seOutput list when is call saveAll()
        Mockito.doAnswer(invocation -> {
            Set<SparqlEndpoint> arg = invocation.getArgument(0);
            seOutput.addAll(arg);
            return null;
        }).when(ser).saveAll(any());

        sedm.update(seInput);
        assertEquals(seInput, seOutput);
    }


    @Property(trials = 100)
    void updateWithNonEmptyDB(int n, int m) {
        assumeThat(n, greaterThanOrEqualTo(0));
        assumeThat(m, greaterThanOrEqualTo(0));

        Set<SparqlEndpoint> seInput = new HashSet<>();
        for (int i = 0; i < n; i++) {
            seInput.add(new SparqlEndpoint("http://localhost:8080/sparql"+ i, "sparql" + i));
        }

        Set<SparqlEndpoint> seDB = new HashSet<>();
        for(int i = 0; i < m; i++) {
            seDB.add(new SparqlEndpoint("http://localhost:8080/sparql"+ i, "sparql" + i));
        }

        SparqlEndpointRepository ser = Mockito.mock(SparqlEndpointRepository.class);
        SparqlEndpointDATAManagement sedm = new SparqlEndpointDATAManagementJPAImpl(ser, null);


        Mockito.when(ser.findAllByOrderById()).thenReturn(new ArrayList<>(seDB));

        // Mock deleteByUrl() method to delete element in list by url
        Mockito.doAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            for (SparqlEndpoint se : seDB) {
                if (se.getUrl().equals(arg)) {
                    seDB.remove(se);
                    break;
                }
            }
            return null;
        }).when(ser).deleteByUrl(anyString());

        // Mock saveAll() method to save element in list
        Mockito.doAnswer(invocation -> {
            Set<SparqlEndpoint> arg = invocation.getArgument(0);
            seDB.addAll(arg);
            return null;
        }).when(ser).saveAll(any());

        sedm.update(seInput);
        assertEquals(seInput, seDB);

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