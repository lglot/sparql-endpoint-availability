package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class SparqlEndpointDATAManagementJPATest {

    public static Stream<Arguments> dataProvider_getSparqlEndpoints() {
        SparqlEndpoint se1 = new SparqlEndpoint("http://localhost:8080/se1", "se1");
        SparqlEndpoint se2 = new SparqlEndpoint("http://localhost:8080/se2", "se2");
        SparqlEndpoint se3 = new SparqlEndpoint("http://localhost:8080/se3", "se3");
        return Stream.of(
                Arguments.of(new HashSet<>(), new HashSet<>()),
                Arguments.of(new HashSet<>(Arrays.asList(se1, se2)), new HashSet<>()),
                Arguments.of(new HashSet<>(Arrays.asList(se1,se2)), new HashSet<>(Arrays.asList(se1,se3))),
                Arguments.of(new HashSet<>(Arrays.asList(se1,se2)), new HashSet<>(Arrays.asList(se1,se2,se3))),
                Arguments.of(new HashSet<>(Arrays.asList(se1,se2,se3)), new HashSet<>(Arrays.asList(se1,se2,se3))),
                Arguments.of(new HashSet<>(), new HashSet<>(Arrays.asList(se1,se2,se3)))
        );
    }


    @ParameterizedTest
    @MethodSource("dataProvider_getSparqlEndpoints")
    void updateWithNonEmptyDB(Set<SparqlEndpoint> seInput, Set<SparqlEndpoint> seDB) {

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
}