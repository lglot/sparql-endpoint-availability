package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatusSummary;
import it.unife.sparql_endpoint_availability.model.management.SparqlEndpointDATAManagement;
import it.unife.sparql_endpoint_availability.config.AppConfig;
import it.unife.sparql_endpoint_availability.config.SparqlEndpointStatusConfig;
import it.unife.sparql_endpoint_availability.service.sparqlEndpointQuery.SparqlEndpointCheckService;
import org.apache.jena.ext.com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.*;
import it.unife.sparql_endpoint_availability.service.resourceManagement.SparqlEndpointListFileManagement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

//Classe controller che gestisce le richieste del client
@Controller
@RequestMapping(path = "/sparql-endpoint-availability", produces = "text/html")
public class SparqlEndpointAvailabilityController {

    private final SparqlEndpointDATAManagement sparqlEndpointDATAManagement;
    private final SparqlEndpointStatusConfig statusConfig;

    private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointAvailabilityController.class);

    public SparqlEndpointAvailabilityController(SparqlEndpointDATAManagement sparqlEndpointDATAManagement,
            SparqlEndpointStatusConfig statusConfig) {
        this.sparqlEndpointDATAManagement = sparqlEndpointDATAManagement;
        this.statusConfig = statusConfig;
    }

    // metodo per forzare l'aggiornamento degli sparql endopoint da parte del client
    @GetMapping(path = "/update")
    public @ResponseBody String update() {

        /* Get Application Context */
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        /* Get istance of class that provide access to sparql Endpoint file */
        SparqlEndpointListFileManagement sparqlEndpointListFileManagament = ctx
                .getBean(SparqlEndpointListFileManagement.class);

        /* Read spaql endpoint URL from resource and save them to DATA */
        sparqlEndpointDATAManagement.update(sparqlEndpointListFileManagament.getSparqlEndpoints());

        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointDATAManagement.getAllSparqlEndpoints();

        SparqlEndpointCheckService sparqlEndpointCheckService = ctx.getBean(SparqlEndpointCheckService.class);
        sparqlEndpointDATAManagement.saveStatuses(sparqlEndpointCheckService.executeCheck(sparqlEndpointList));

        logger.info(
                "Executed manually check terminated in date " + new Timestamp(System.currentTimeMillis()).toString());
        return "updated";
    }

    /*
     * Metodo che viene chiamato su richiesta del client per ottenere la lista degli
     * sparql endopoint
     * dove vengono prelevati le informazioni sulla disponibilità dell'ultima
     * settimana degli sparlq enpoint
     * e settati come parametri del modello dei dati che andrà a riempire il
     * template lato VIEW
     * 
     * In particolare viene richiesto al gestore del database di ottenere la lista
     * con le query sparql effettuate nella settimana;
     * se la lista ottenuta è vuota (ovvero non c'è nessuno sparql endopoint sul
     * database oppure non è ancora stata acquistia alcuno informazione
     * sulla loro disponibilità essendo il server da poco avviato) si rimanda
     * indietro un messaggio di errore.
     * Altrimenti, per ogni sparlq endpoint, viene assegnato un'etichetta indicante
     * lo stato indicante la disponibilià attuale,
     * giornaliera e settimanale
     */
    @GetMapping(path = "")
    public String view(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang, Model model) {

        /* HTTP PARAMETERS */
        SortedMap<Number, SparqlEndpointStatusSummary> sparqlStatusMap = new TreeMap<>();
        int numberActive = 0;
        Date lastUpdate = null;
        LocalDateTime lastUpdateLocal = null;
        Date firstUpdate;
        long weeksPassed = 0;
        long daysPassed = 0;
        String applicationMessage = null;

        Calendar previousWeek = Calendar.getInstance();
        Calendar previousDay = Calendar.getInstance();
        previousWeek.add(Calendar.WEEK_OF_YEAR, -1);
        previousDay.add(Calendar.DAY_OF_YEAR, -1);

        // Ottengo la lista degli sparql enpoint con risultati delle query sparql dal
        // database
        List<SparqlEndpoint> sparqlEndpointList = sparqlEndpointDATAManagement
                .getSparqlEndpointsAfterQueryDate(previousWeek.getTime());

        // controllo se la lista non sia vuota e che sia stata almento eseguita una
        // query sparql
        if (sparqlEndpointList.size() > 0 && sparqlEndpointList.get(0).getSparqlEndpointStatuses().size() > 0) {

            // ottengo data della prima query eseguita dall'avvio del server collegandomi di
            // nuovo al database
            firstUpdate = sparqlEndpointDATAManagement.findFirstQueryDate();

            List<SparqlEndpointStatus> statusTemp = sparqlEndpointList.get(0).getSparqlEndpointStatuses();
            // calcolo data dell'ultima query eseguita
            lastUpdate = statusTemp.get(0).getQueryDate();
            lastUpdateLocal = LocalDateTime.ofInstant(lastUpdate.toInstant(), ZoneId.of("Z"));

            // calcolo i giorni e quindi le settimane passate dalla prima query eseguita
            daysPassed = ChronoUnit.DAYS.between(firstUpdate.toInstant(), lastUpdate.toInstant());
            weeksPassed = daysPassed / 7;

            for (SparqlEndpoint sparqlEndpoint : sparqlEndpointList) {

                // creo un nuovo oggetto di tipo DTO (Data tranfer object),
                // che conterrà le informazioni di ogni sparlq endpoint e che sarà poi
                // trasferito al VIEWW LAYER
                SparqlEndpointStatusSummary statusSummary = new SparqlEndpointStatusSummary();
                statusSummary.setURL(sparqlEndpoint.getUrl());

                // prelevo la lista con i risultati delle query sparql
                List<SparqlEndpointStatus> statusList = sparqlEndpoint.getSparqlEndpointStatuses();

                // calcolo il numero totale delle query eseuguite per lo sparql endpoint
                // dell'attuale iterazione
                double totalStatus = statusList.size();

                // booleano che mi dice l'endpoint in questa iterazione è attivo attualmente
                boolean activeFound = false;

                // variabili per contare il numero delle volte che l'endopint di questa
                // iterazione è risultato
                // attivo in questa giornata e in questa settimana
                double activeCounterThisDay = 0;
                double activeCounterThisWeek = 0;

                // controllo se l'endpoint è attivo attualmente
                if (statusList.get(0).isActive()) {
                    statusSummary.setStatusString(statusConfig.getActive());
                    activeFound = true;
                    activeCounterThisDay++;
                    activeCounterThisWeek++;
                    numberActive++;
                } else if (weeksPassed < 1) {
                    statusSummary.setStatusString(statusConfig.getGeneralInactive());
                    activeFound = true;
                }

                int i = 1;
                Date yesterday = previousDay.getTime();

                // controllo se e quante volte l'endopint è risultativo nelle ultime 24 ore
                while (i < totalStatus && statusList.get(i).getQueryDate().after(yesterday)) {

                    SparqlEndpointStatus status = statusList.get(i);

                    if (status.isActive()) {
                        if (!activeFound) {
                            statusSummary.setStatusString(statusConfig.getInactiveLessday());
                            activeFound = true;
                        }
                        activeCounterThisDay++;
                        activeCounterThisWeek++;
                    }
                    i++;
                }

                int totalStatusThisDay = i;
                // controllo se e quante volte l'endpoint è risultato attivo negli ultimi 7
                // giorni (la
                // lista non contiene stati oltre la settimana passata)
                for (SparqlEndpointStatus status : Iterables.skip(statusList, totalStatusThisDay)) {

                    if (status.isActive()) {
                        if (!activeFound) {
                            statusSummary.setStatusString(statusConfig.getInactiveLessweek());
                            activeFound = true;
                        }
                        activeCounterThisWeek++;
                    }
                }
                if (!activeFound) {
                    statusSummary.setStatusString(statusConfig.getInactiveMoreweek());
                }

                // calcolo uptime
                // hours in a week 168
                int hoursWeek = 168;
                int hoursDay = 24;
                if (totalStatus >= 168) {
                    statusSummary.setUptimelast7d((activeCounterThisWeek / totalStatus));
                } else {
                    statusSummary.setUptimelast7d(-1);
                }
                if (totalStatusThisDay >= 24) {
                    statusSummary.setUptimeLast24h(activeCounterThisDay / totalStatusThisDay);
                } else {
                    statusSummary.setUptimeLast24h(-1);
                }
                statusSummary.setName(sparqlEndpoint.getName());
                sparqlStatusMap.put(sparqlEndpoint.getId(), statusSummary);

            }

        } else {
            applicationMessage = "No SPARQL endpoint DATA found";
        }

        String lastUpdateLocalString = "No date";
        if (lastUpdateLocal != null) {
            lastUpdateLocalString = lastUpdateLocal.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            lastUpdateLocalString = lastUpdateLocalString.split("T")[0] + " " + lastUpdateLocalString.split("T")[1];
        }

        model.addAttribute("applicationMessage", applicationMessage);
        model.addAttribute("sparqlStatusMap", sparqlStatusMap);
        model.addAttribute("numberActive", numberActive);
        model.addAttribute("lastUpdate", lastUpdate);

        model.addAttribute("lastUpdateLocal", lastUpdateLocalString);
        model.addAttribute("lang", lang);
        model.addAttribute("daysPassed", daysPassed);

        return "view";

    }

}
