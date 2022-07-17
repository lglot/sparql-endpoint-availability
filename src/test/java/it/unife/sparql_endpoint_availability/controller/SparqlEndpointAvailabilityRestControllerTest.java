package it.unife.sparql_endpoint_availability.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unife.sparql_endpoint_availability.dto.SparqlEndpointDTO;
import it.unife.sparql_endpoint_availability.exception.SparqlEndpointNotFoundException;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.service.fileReader.SparqlFileReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SparqlEndpointAvailabilityRestController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SparqlEndpointAvailabilityRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SparqlEndpointDATAManagement sedm;

    private List<SparqlEndpoint> sparqlEndpoints;

    @BeforeAll
    void init() {
        sparqlEndpoints = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            SparqlEndpoint se = SparqlEndpoint.builder()
                    .id((long) i)
                    .url("url" + i)
                    .name("name" + i)
                    .build();
            SparqlEndpointStatus status = SparqlEndpointStatus.builder()
                    .sparqlEndpoint(se)
                    .queryDate(new Date())
                    .active(i%2==0)
                    .build();
            SparqlEndpointStatus statusOld = SparqlEndpointStatus.builder()
                    .sparqlEndpoint(se)
                    .queryDate(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7))
                    .active(i%2==0)
                    .build();
            se.setSparqlEndpointStatuses(Arrays.asList(status, statusOld));
            sparqlEndpoints.add(se);
        }
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getAllSparqlEndpoints() throws Exception {
        Mockito.when(sedm.getSparqlEndpointsWithCurrentStatus()).thenReturn(sparqlEndpoints);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/sparql-endpoint-availability");
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> {
                    assertEquals(200, mvcResult.getResponse().getStatus());
                    List<SparqlEndpointDTO> seResponse = new ObjectMapper()
                            .readValue(mvcResult.getResponse().getContentAsString(),
                                    new TypeReference<List<SparqlEndpointDTO>>() {});
                    assertEquals(5, seResponse.size());
                });
    }

    @Test
    void getAllSparqlEndpointsNoPermission() throws Exception {
        Mockito.when(sedm.getSparqlEndpointsWithCurrentStatus()).thenReturn(sparqlEndpoints);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/sparql-endpoint-availability");
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> assertEquals(401, mvcResult.getResponse().getStatus()));
    }


    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getSparqlEndpointById() throws Exception {

        Mockito.when(sedm.getSparqlEndpointWithCurrentStatusById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return sparqlEndpoints.stream()
                    .filter(se -> se.getId().equals(id))
                    .findFirst()
                    .orElseThrow(SparqlEndpointNotFoundException::new);
        });
        //random id between 1 and 5
        long id = (long) (Math.random() * 4) + 1;
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/sparql-endpoint-availability/"+id);
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> {
                    assertEquals(200, mvcResult.getResponse().getStatus());
                    SparqlEndpointDTO seResponse = new ObjectMapper()
                            .readValue(mvcResult.getResponse().getContentAsString(),SparqlEndpointDTO.class);
                    assertEquals(sparqlEndpoints.get((int) id-1).getUrl(), seResponse.getUrl());
                });
        //id not found
        long id2 = (long) (Math.random() * 10) + 10;
        requestBuilder = MockMvcRequestBuilders.get("/sparql-endpoint-availability/"+id2);
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> assertEquals(404, mvcResult.getResponse().getStatus()));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getCurrentlyActiveSparqlEndpoints() throws Exception {
        Mockito.when(sedm.getCurrentlyActiveSparqlEndpoints()).thenAnswer(invocation -> sparqlEndpoints.stream()
                .filter(se -> se.getSparqlEndpointStatuses().stream()
                        .anyMatch(SparqlEndpointStatus::isActive))
                .collect(Collectors.toList()));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                get("/sparql-endpoint-availability/status/current/active");
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> {
                    assertEquals(200, mvcResult.getResponse().getStatus());
                    List<SparqlEndpointDTO> seResponse = new ObjectMapper()
                            .readValue(mvcResult.getResponse().getContentAsString(),
                                    new TypeReference<List<SparqlEndpointDTO>>() {});
                    assertEquals(2, seResponse.size());
                });
    }
}