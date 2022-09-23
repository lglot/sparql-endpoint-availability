package it.unife.sparql_endpoint_availability.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unife.sparql_endpoint_availability.dto.SparqlEndpointDTO;
import it.unife.sparql_endpoint_availability.exception.SparqlEndpointNotFoundException;
import it.unife.sparql_endpoint_availability.jwt.JwtAuthenticationEntryPoint;
import it.unife.sparql_endpoint_availability.jwt.JwtConfig;
import it.unife.sparql_endpoint_availability.jwt.JwtSecretKey;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import it.unife.sparql_endpoint_availability.security.PasswordConfiguration;
import it.unife.sparql_endpoint_availability.security.SecurityConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(SparqlEndpointAvailabilityRestController.class)
@Import({SecurityConfiguration.class,
        JwtConfig.class,
        JwtAuthenticationEntryPoint.class,
        JwtSecretKey.class, PasswordConfiguration.class})
class SparqlEndpointAvailabilityRestControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SparqlEndpointManagement sedm;

    @MockBean
    private AppUserManagement appUserManagement;


    private List<SparqlEndpoint> sparqlEndpoints;
    private final String BASE_URL_API = "/api/endpoints";

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
    @WithMockUser(roles = "USER")
    void getAllSparqlEndpoints() throws Exception {
        Mockito.when(sedm.getSparqlEndpointsWithCurrentStatus()).thenReturn(sparqlEndpoints);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_URL_API);
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
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_URL_API);
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> assertEquals(401, mvcResult.getResponse().getStatus()));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getSparqlEndpointByUrl() throws Exception {

        Mockito.when(sedm.getSparqlEndpointByUrl(anyString())).thenAnswer(invocation -> {
            String url = invocation.getArgument(0);
            return sparqlEndpoints.stream()
                    .filter(se -> se.getUrl().equals(url))
                    .findFirst()
                    .orElseThrow(SparqlEndpointNotFoundException::new);
        });
        //random id between 0 and 4
        long id = (long) (Math.random() * 4);
        SparqlEndpoint se = sparqlEndpoints.get((int) id);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_URL_API+"/url?url="+se.getUrl());
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> {
                    assertEquals(200, mvcResult.getResponse().getStatus());
                    SparqlEndpointDTO seResponse = new ObjectMapper()
                            .readValue(mvcResult.getResponse().getContentAsString(),SparqlEndpointDTO.class);
                    assertEquals(se.getUrl(), seResponse.getUrl());
                });
        // test not found
        requestBuilder = MockMvcRequestBuilders.get(BASE_URL_API+"/url?url=notfound");
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> assertEquals(404, mvcResult.getResponse().getStatus()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCurrentlyActiveSparqlEndpoints() throws Exception {
        Mockito.when(sedm.getCurrentlyActiveSparqlEndpoints()).thenAnswer(invocation -> sparqlEndpoints.stream()
                .filter(se -> se.getSparqlEndpointStatuses().stream()
                        .anyMatch(SparqlEndpointStatus::isActive))
                .collect(Collectors.toList()));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                get(BASE_URL_API+"/status/current/active");
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> {
                    assertEquals(200, mvcResult.getResponse().getStatus());
                    List<SparqlEndpointDTO> seResponse = new ObjectMapper()
                            .readValue(mvcResult.getResponse().getContentAsString(),
                                    new TypeReference<List<SparqlEndpointDTO>>() {});
                    assertEquals(2, seResponse.size());
                });
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createSparqlEndpoint() throws Exception {

        SparqlEndpoint se = SparqlEndpoint.builder()
                .url("new_url")
                .name("new_name")
                .build();

        Mockito.when(sedm.createSparqlEndpoint(se)).thenAnswer(invocation -> {
            SparqlEndpoint se2 = invocation.getArgument(0);
            se2.setId((long) (sparqlEndpoints.size()+1));
            sparqlEndpoints.add(se2);
            return se;
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(BASE_URL_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(se));

        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> {
                    assertEquals(201, mvcResult.getResponse().getStatus());
                    SparqlEndpointDTO seResponse = new ObjectMapper()
                            .readValue(mvcResult.getResponse().getContentAsString(),SparqlEndpointDTO.class);
                    assertEquals(se.getUrl(), seResponse.getUrl());
                    //assert that sparqlEndpoint url is present in the list of sparqlEndpoints
                    assertTrue(sparqlEndpoints.stream()
                            .anyMatch(se3 -> se3.getUrl().equals(se.getUrl())));
                });
        sparqlEndpoints.removeIf(se3 -> se3.getUrl().equals(se.getUrl()));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSparqlEndpointByUrl() throws Exception {

        Mockito.when(sedm.updateSparqlEndpointByUrl(anyString(), any())).thenAnswer(invocation -> {
            String url = invocation.getArgument(0);
            SparqlEndpoint se = invocation.getArgument(1);
            sparqlEndpoints.stream()
                    .filter(se2 -> se2.getUrl().equals(url))
                    .findFirst()
                    .ifPresent(se2 -> se2.setName(se.getName()));
            return se;
        });

        SparqlEndpoint se = sparqlEndpoints.get(0);
        SparqlEndpoint newSe = SparqlEndpoint.builder()
                .url(se.getUrl())
                .name("new name")
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(BASE_URL_API+"/url/?url="+newSe.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newSe));

        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> {
                    assertEquals(200, mvcResult.getResponse().getStatus());
                    SparqlEndpointDTO seResponse = new ObjectMapper()
                            .readValue(mvcResult.getResponse().getContentAsString(),SparqlEndpointDTO.class);
                    assertEquals(newSe.getUrl(), seResponse.getUrl());
                    assertEquals(newSe.getName(), seResponse.getName());
                    //assert that new name is present in the list of sparqlEndpoints
                    assertTrue(sparqlEndpoints.stream()
                            .anyMatch(se3 -> se3.getUrl().equals(newSe.getUrl()) && se3.getName().equals(newSe.getName())));
                });
        sparqlEndpoints.set(0, se);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSparqlEndpointByUrl() throws Exception {
        Mockito.doAnswer(invocation -> {
            String url = invocation.getArgument(0);
            sparqlEndpoints.removeIf(se -> se.getUrl().equals(url));
            return true;
        }).when(sedm).deleteSparqlEndpointByUrl(anyString());

        SparqlEndpoint se = sparqlEndpoints.get(0);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(BASE_URL_API+"/url/?url="+se.getUrl());
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> {
                    assertEquals(200, mvcResult.getResponse().getStatus());
                    //assert that sparqlEndpoint url is not present in the list of sparqlEndpoints
                    assertFalse(sparqlEndpoints.stream()
                            .anyMatch(se3 -> se3.getUrl().equals(se.getUrl())));
                });
        sparqlEndpoints.add(0, se);
    }

    //post with no permission
    @Test
    @WithMockUser(roles = "USER")
    void createSparqlEndpointNoPermission() throws Exception {

        SparqlEndpoint se = SparqlEndpoint.builder()
                .url("new_url")
                .name("new_name")
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(BASE_URL_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(se));

        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> assertEquals(403, mvcResult.getResponse().getStatus()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateSparqlEndpointByUrlNoPermission() throws Exception {

        SparqlEndpoint se = sparqlEndpoints.get(0);
        SparqlEndpoint newSe = SparqlEndpoint.builder()
                .url(se.getUrl())
                .name("new name")
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(BASE_URL_API+"/url/?url="+newSe.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newSe));

        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> assertEquals(403, mvcResult.getResponse().getStatus()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteSparqlEndpointByUrlNoPermission() throws Exception {
        SparqlEndpoint se = sparqlEndpoints.get(0);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(BASE_URL_API + "/url/?url=" + se.getUrl());
        mockMvc.perform(requestBuilder)
                .andExpect(mvcResult -> assertEquals(403, mvcResult.getResponse().getStatus()));
    }

}