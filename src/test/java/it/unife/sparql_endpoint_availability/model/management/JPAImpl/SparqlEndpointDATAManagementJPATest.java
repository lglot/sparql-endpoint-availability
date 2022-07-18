package it.unife.sparql_endpoint_availability.model.management.JPAImpl;

import it.unife.sparql_endpoint_availability.exception.SparqlEndpointNotFoundException;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.model.repository.SparqlEndpointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.*;
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
    void saveAllIfNotExists(Set<SparqlEndpoint> seInput, Set<SparqlEndpoint> seDB) {

        SparqlEndpointRepository ser = Mockito.mock(SparqlEndpointRepository.class);
        SparqlEndpointDATAManagement sedm = new SparqlEndpointDATAManagementJPAImpl(ser, null);

        Mockito.when(ser.findAllByOrderById()).thenReturn(new ArrayList<>(seDB));

        // Mock saveAll() method to save element in list
        Mockito.doAnswer(invocation -> {
            Set<SparqlEndpoint> arg = invocation.getArgument(0);
            seDB.addAll(arg);
            return null;
        }).when(ser).saveAll(any());

        sedm.saveAllIfNotExists(seInput);
        //assert that the list of sparql endpoints in the database contains all elements of the input list
        assertContainsAll(seDB, seInput);


    }

    private void assertContainsAll(Set<SparqlEndpoint> seDB, Set<SparqlEndpoint> seInput) {
        for (SparqlEndpoint se : seInput) {
            assertTrue(seDB.contains(se));
        }
    }

    @Test
    void getSparqlEndpointById() throws SparqlEndpointNotFoundException {
        SparqlEndpointRepository ser = Mockito.mock(SparqlEndpointRepository.class);
        SparqlEndpointDATAManagement sedm = new SparqlEndpointDATAManagementJPAImpl(ser, null);
        Mockito.when(ser.findById(anyLong())).thenReturn(Optional.of(new SparqlEndpoint("http://localhost:8080/se1", "se1")));
        SparqlEndpoint se = sedm.getById(1L);

        assertEquals("se1", se.getName());
    }
}