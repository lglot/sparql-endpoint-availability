package it.unife.sparql_endpoint_availability;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    // @Test
    // public void greetingShouldReturnDefaultMessage() throws Exception {
    // SparqlEndpoint se = this.restTemplate.getForObject("http://localhost:" + port
    // + "/sparql-endpoint-availability/1",
    // application / json));
    // assertThat(se.getId().isEqualTo(1));
    // }
}