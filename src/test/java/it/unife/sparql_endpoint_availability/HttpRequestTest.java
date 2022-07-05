package it.unife.sparql_endpoint_availability;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.model.management.JPAImpl.SparqlEndpointDATAManagementJPAImpl;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// public class HttpRequestTest {
//
//     @LocalServerPort
//     private int port;
//
//     @Autowired
//     private TestRestTemplate restTemplate;
//
//     @Test
//     public void getSparqlEndpointApiTest() throws Exception {
//         Iterable<SparqlEndpoint> se = this.restTemplate.getForObject(
//                 "http://localhost:" + port + "/sparql-endpoint-availability/status/current", );
//
//         assertThat(se.getId()).isEqualTo(1L);
//     }
// }