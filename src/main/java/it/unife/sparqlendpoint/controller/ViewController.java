package it.unife.sparqlendpoint.controller;

import it.unife.sparqlendpoint.service.config.Config;
import it.unife.sparqlendpoint.service.thread.SparqlEndpointsQueryThread;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.io.FileUtils.getFile;

@Controller
public class ViewController {

    @GetMapping("/view")
    public String view(Model model) {

        List<String> sparqlList = readFromFile();
        HashMap<String,Boolean> sparqlHashMap = new HashMap<>();
        int numberActive = 0;
        final int queryNumberByThread = 5;

        /*MultiThread Query*/
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        List<SparqlEndpointsQueryThread> threads = new ArrayList<>();

        for(int i=0;i<sparqlList.size();i=i+queryNumberByThread){
            SparqlEndpointsQueryThread thread = (SparqlEndpointsQueryThread)context.getBean("sparqlEndpointsQueryThread");
            threads.add(thread);
            thread.setPartialSparqlEndpointsList(sparqlList.subList(i, Math.min(i + queryNumberByThread, sparqlList.size())));
            thread.start();
        }

        try{
            for(SparqlEndpointsQueryThread thread : threads) {
                thread.join();
                sparqlHashMap.putAll(thread.getSparqlHashMap());
                numberActive=numberActive+thread.getNumberActive();
            }
        }catch (InterruptedException ignored){ }


        model.addAttribute("sparqlHashMap",sparqlHashMap);
        model.addAttribute("numberActive",numberActive);
        return "view";
    }

    private static List<String> readFromFile() {

        List<String> sparqlList = new ArrayList<>();

        try {

            String filePath = Objects.requireNonNull(ViewController.class.getClassLoader().getResource(Config.SPARQL_ENDPOINTS_LIST_FILENAME)).getFile();
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            while ((line = br.readLine())!= null){
                sparqlList.add(line.toLowerCase());
            }
        }catch(IOException e) {
            throw new RuntimeException(e);
        }
        return sparqlList;
    }
}

