package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.exception.SparqlEndpointAlreadyExistsException;
import it.unife.sparql_endpoint_availability.exception.SparqlEndpointNotFoundException;
import it.unife.sparql_endpoint_availability.jwt.JwtConfig;
import it.unife.sparql_endpoint_availability.jwt.TokenManager;
import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointManagement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SparqlEndpointRestControllerSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SparqlEndpointManagement sedm;
    @Autowired
    private AppUserManagement aum;

    @Autowired
    SecretKey secretKey;
    @Autowired
    JwtConfig jwtConfig;

    private List<SparqlEndpoint> sparqlEndpoints;
    private AppUser test_user;
    private AppUser test_admin;
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
                    .queryDate(LocalDateTime.now())
                    .active(i%2==0)
                    .build();

            SparqlEndpointStatus statusOld = SparqlEndpointStatus.builder()
                    .sparqlEndpoint(se)
                    .queryDate(LocalDateTime.now().minusWeeks(1))
                    .active(i%2==0)
                    .build();
            se.setSparqlEndpointStatuses(Arrays.asList(status, statusOld));
            sparqlEndpoints.add(se);
            sedm.createSparqlEndpoint(se);
        }

        // create test user
        test_admin = aum.saveUser("test_admin", "test_admin", "admin");
        test_user = aum.saveUser("test_user", "test_user", "user");

    }

    private static String getJwtToken(AppUser user, JwtConfig jwtConfig, SecretKey secretKey) {
        return TokenManager.createToken(user.getUsername(),
                user.getAuthorities(),
                jwtConfig.getExpirationTimeAfertDays(),
                secretKey);

    }

    @AfterAll
    void clean() {
        sparqlEndpoints.forEach(se -> {
            try {
                sedm.deleteSparqlEndpointByUrl(se.getUrl());
            } catch (SparqlEndpointNotFoundException e) {
                e.printStackTrace();
            }
        });
        aum.deleteUser(test_admin.getUsername());
        aum.deleteUser(test_user.getUsername());
    }

    @Test
    void getAllSparqlEndpoints() throws Exception {

        String token = getJwtToken(test_user, jwtConfig, secretKey);
        assertNotNull(token);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL_API)
                        .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasSize(sparqlEndpoints.size())));
    }

    @Test
    void getSparqlEndpointByUrl() throws Exception {

        String url = sparqlEndpoints.get(0).getUrl();
        String token = getJwtToken(test_user, jwtConfig, secretKey);
        assertNotNull(token);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL_API + "/url/?url=" + url)
                        .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value(url));
    }

    @Test
    void getSparqlEndpointByIdUnauthorized() throws Exception {

        String token = getJwtToken(test_user, jwtConfig, secretKey);
        assertNotNull(token);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL_API + "/" + sparqlEndpoints.get(0).getId())
                        .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void createSparqlEndpoint() throws Exception {

        String token = getJwtToken(test_admin, jwtConfig, secretKey);
        assertNotNull(token);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL_API)
                        .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"url\",\"name\":\"name\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        sedm.deleteSparqlEndpointByUrl("url");
    }

    @Test
    void createSparqlEndpointUnauthorized() throws Exception {

        String token = getJwtToken(test_user, jwtConfig, secretKey);
        assertNotNull(token);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL_API)
                        .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"url\",\"name\":\"name\"}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateSparqlEndpoint() throws Exception {


        String new_url = "new_url";
        String new_name = "new_name";

        String token = getJwtToken(test_admin, jwtConfig, secretKey);
        assertNotNull(token);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL_API + "/url/?url=" + sparqlEndpoints.get(0).getUrl())
                        .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"url\":\"%s\",\"name\":\"%s\"}", new_url, new_name)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value(new_url))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(new_name));

        SparqlEndpoint se = sedm.getSparqlEndpointByUrl(new_url);
        assertNotNull(se);

        sedm.updateSparqlEndpointByUrl(new_url, sparqlEndpoints.get(0));

    }

    @Test
    void updateSparqlEndpointNotFound() throws Exception {


        String new_url = "new_url";
        String new_name = "new_name";

        String token = getJwtToken(test_admin, jwtConfig, secretKey);
        assertNotNull(token);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL_API + "/url/?url=" + Long.MAX_VALUE)
                        .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"url\":\"%s\",\"name\":\"%s\"}", new_url, new_name)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateSparqlEndpointUnauthorized() throws Exception {


        String new_url = "new_url";
        String new_name = "new_name";

        String token = getJwtToken(test_user, jwtConfig, secretKey);
        assertNotNull(token);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL_API + "/url/?url=" + sparqlEndpoints.get(0).getUrl())
                        .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"url\":\"%s\",\"name\":\"%s\"}", new_url, new_name)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void deleteSparqlEndpoint() throws Exception {

        String token = getJwtToken(test_admin, jwtConfig, secretKey);
        assertNotNull(token);
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL_API + "/url/?url=" + sparqlEndpoints.get(0).getUrl())
                        .header(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertThrows(SparqlEndpointNotFoundException.class, () -> sedm.getSparqlEndpointByUrl(sparqlEndpoints.get(0).getUrl()));


        // ripristino dati
        sedm.createSparqlEndpoint(sparqlEndpoints.get(0));
    }




}
