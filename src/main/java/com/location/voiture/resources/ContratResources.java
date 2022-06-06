package com.location.voiture.resources;


import com.location.voiture.dao.ContratDao;
import com.location.voiture.domain.EnLocationException;
import com.location.voiture.domain.ExceptionHandling;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.dto.RevenuData;
import com.location.voiture.models.Contrat;
import com.location.voiture.models.RemainingDaysOfContrat;
import com.location.voiture.services.IContratService;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/contrat")
public class ContratResources extends ExceptionHandling {
    @Autowired
    private IContratService contratService;
    @Autowired
    private ContratDao contratDao;

    @GetMapping
    public List<Contrat> getAllContrat() {
        return contratService.getAllContrat();
    }
    @GetMapping("/revenu/{year}/{voitureId}")
    public List<RevenuData> getRevenu(@PathVariable("year")short year, @PathVariable("voitureId") long voitureId ) {
        return  contratService.getRevenu(year,voitureId);
    }

    @GetMapping("/en_location")
    public List<Contrat> getContartsEnCours(){
        return contratService.getContratsEncours();
    }


    @GetMapping("/remaining_days")
    public List<RemainingDaysOfContrat> getRemainingDaysOfContrat(){
        return contratService.getRemainingDaysOfContrat();
    }

    @GetMapping("all")
    public List<Contrat> AllContrat(@RequestParam(required = false, value = "sort") String sort) {
        return  contratDao.findAllByDriverOne_PrenomContainingOrNumContratContainingOrDateCreatedLike
                (sort,sort, LocalDateTime.parse(sort));
    }
    @GetMapping("/retour")
    public List<Contrat> getAllContratByDateRetour(){
       return contratService.getAllContratByDateRetour();
    }

    @GetMapping("by_client/{driver1}/{driver2}")
    public List<Contrat> findContratByClientsId(@PathVariable long driver2, @PathVariable long driver1) {
        return contratService.findContratByClientsId(driver1, driver2);
    }

    @GetMapping("/{contratId}")
    public Contrat getOneContrat(@PathVariable("contratId") long contratID) throws ResourceNotFoundException {
        return contratService.getOneContrat(contratID);
    }

    @GetMapping("/list/contrat_by/{matricule}")
    public List<Contrat> findContratByVoitureMatricule(@PathVariable("matricule") String matricule) {
        return contratService.findContratByVoitureMatricule(matricule);
    }
    @PostMapping("/contrat-pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestParam("contratId") long contratId) throws IOException, ResourceNotFoundException, JRException {
        byte[] contratPdf = contratService.generateContratPdf(contratId);
        HttpHeaders headers = new HttpHeaders();
        //headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=contrat"+contratId+".pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(contratPdf);
    }

    @DeleteMapping("/{contratId}")
    public ResponseEntity<?> deleteContrat(@PathVariable("contratId") long contratId) throws ResourceNotFoundException {
        contratService.deleteContrat(contratId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Contrat> addContra(@RequestParam(value = "contratId", required = false) long contratId,
                                             @RequestParam("clnt1") long clnt1,
                                             @RequestParam(value = "clnt2", required = false) String clnt2,
                                             @RequestParam("voitureId") long voitureId,
                                             @RequestParam(value = "numDay", required = false) int numDay,
                                             @RequestParam(value = "prix", required = false) Double prix,
                                             @RequestParam(value = "avance", required = false) Double avance,
                                             @RequestParam(value = "depart") String depart,
                                             @RequestParam(value = "retour", required = false) String retour,
                                             @RequestParam(value = "isPayer", required = false) boolean isPayer,
                                             @RequestParam(value = "caution", required = false) String caution)
            throws EnLocationException, ResourceNotFoundException {
        double parseCautionToDouble;
        if (!caution.equals("null")) {
            parseCautionToDouble = Double.parseDouble(caution);
        } else parseCautionToDouble = 0;

        LocalDateTime localDateTimeD = LocalDateTime.parse(depart, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime localDateTimeR = null;


        if (!retour.equals("null") && !retour.equals("")) {
            localDateTimeR = LocalDateTime.parse(retour, DateTimeFormatter.ISO_DATE_TIME);
        }
        ;
        long a = 0;
        if (StringUtils.isNoneEmpty(clnt2) && !clnt2.equals("undefined")) {
            a = Long.parseLong(clnt2);
        }

        Contrat contrat = contratService.newContrat(contratId, clnt1, a, voitureId, numDay, prix, avance, localDateTimeD, localDateTimeR, isPayer, parseCautionToDouble);
        return new ResponseEntity<>(contrat, HttpStatus.CREATED);
    }

}
