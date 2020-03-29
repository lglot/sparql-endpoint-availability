/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unife.sparql_endpoint_availability.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.ext.com.google.common.base.Charsets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Giuseppe Cota <giuseppe.cota@unife.it>
 */
public class ParseHTMLAvailableEndpoints {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        InputStream input = ParseHTMLAvailableEndpoints.class.getResourceAsStream("/https _sparqles.ai.wu.ac.at_availability.html");
        Document doc = Jsoup.parse(input, "UTF-8", "");
        Elements endpointElements = doc.select("#table_current tbody tr");

        List<SparqlEndpoint> sparqlEndpoints = new ArrayList<>();

        for (Element el : endpointElements) {
            SparqlEndpoint endpoint = new SparqlEndpoint();
            String endpointName = el.select("a[href]").get(0).text();
            String endpointURL = el.select("a[href]").get(0).attr("href");
            endpointURL = endpointURL.split("uri=")[1];
            //endpointURL = Jsoup.parse(endpointURL).text();
            endpointURL = URLDecoder.decode(endpointURL, Charsets.UTF_8);
            endpoint.setName(endpointName);
            endpoint.setServiceURL(endpointURL);
            sparqlEndpoints.add(endpoint);
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter writer   = objectMapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(new File("sparql_endpoints.json"), sparqlEndpoints);
        
        //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).writeValue(new File("sparql_test.json"), sparqlEndpoints);

    }

}
