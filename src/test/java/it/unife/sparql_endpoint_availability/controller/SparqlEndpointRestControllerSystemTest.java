package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.exception.SparqlEndpointAlreadyExistsException;
import it.unife.sparql_endpoint_availability.jwt.JwtConfig;
import it.unife.sparql_endpoint_availability.jwt.JwtSecretKey;
import it.unife.sparql_endpoint_availability.jwt.TokenManager;
import it.unife.sparql_endpoint_availability.model.entity.AppGrantedAuthority;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SparqlEndpointRestControllerSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SparqlEndpointManagement sedm;

    @Autowired
    JwtSecretKey jwtSecretKey;
    @Autowired
    JwtConfig jwtConfig;

    private List<SparqlEndpoint> sparqlEndpoints;
    private final String BASE_URL_API = "/api/endpoints";

    @BeforeAll
    void init() throws SparqlEndpointAlreadyExistsException {
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
            sedm.createSparqlEndpoint(se);
        }
    }

    @Test
    void getAllSparqlEndpoints() throws Exception {

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new AppGrantedAuthority("ROLE_USER"));

        String token = TokenManager.createToken("user", authorities, 1, jwtSecretKey.secretKey());

        assertNotNull(token);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL_API)
                        .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasSize(sparqlEndpoints.size())));
    }
}
