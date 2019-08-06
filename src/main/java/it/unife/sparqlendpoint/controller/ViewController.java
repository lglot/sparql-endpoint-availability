package it.unife.sparqlendpoint.controller;

import it.unife.sparqlendpoint.service.config.Config;
import org.apache.jena.query.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class ViewController {

    @GetMapping("/view")
    public String view(Model model) {

        String sparqlQueryString = "SELECT * WHERE {?s ?p ?o} LIMIT 1";

        List<String> sparqlList = readFromFile();

        HashMap<String,Boolean> sparqlHashMap = new HashMap<>();

        int numberActive = 0;

        for (String service: sparqlList) {

            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, sparqlQueryString)) {
         // qexec.setTimeout(60, TimeUnit.SECONDS);
                ResultSet rs = qexec.execSelect();
                if (rs.hasNext()) {
                    sparqlHashMap.put(service,true);
                    numberActive++;
                    //model.addAttribute("active",true);
                    //model.addAttribute("subject",rs.nextSolution().get("s").toString());
                }
            } catch(Exception e) {
                //model.addAttribute("active",false);
                sparqlHashMap.put(service,false);
            }
        }

        model.addAttribute("sparqlHashMap",sparqlHashMap);
        model.addAttribute("numberActive",numberActive);
        return "view";
    }

    private static List<String> readFromFile() {

        List<String> sparqlList = new ArrayList<>();

        try {

            File file = new File(Config.SPARQL_ENDPOINTS_LIST_PATH);
            FileReader fileReader = new FileReader(file);
            //InputStream inputStream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            while ((line = br.readLine())!= null){
                sparqlList.add(line.toLowerCase());
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        return sparqlList;
    }
}

