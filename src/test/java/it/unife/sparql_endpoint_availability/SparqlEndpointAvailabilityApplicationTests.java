package it.unife.sparql_endpoint_availability;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.web.server.LocalServerPort;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SparqlEndpointAvailabilityApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
public class SparqlEndpointAvailabilityApplicationTests {

    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @Autowired
    WebApplicationContext webApplicationContext;

//    public String mapToJson(Object obj) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.writeValueAsString(obj);
//    }
//
//
//    public <T> T mapFromJson(String json, Class<T> clazz)
//            throws JsonParseException, JsonMappingException, IOException {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.readValue(json, clazz);
//    }

    @Test
    public void contextLoads() {

    }

    @BeforeAll
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Disabled
    @Test
    public void getSparqlEndpointApiTest() throws Exception {
        String uri = "/sparql-endpoint-availability/1";
        MvcResult result = mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        assertEquals(200,result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        System.out.println("PORCOOOOOOO" + content);
        SparqlEndpoint.OnlySparqlEndpoint se = new ObjectMapper().readValue(content, SparqlEndpoint.OnlySparqlEndpoint.class);
        assertEquals(se.getId(), 1L);
    }



}
