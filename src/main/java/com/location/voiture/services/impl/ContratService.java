package com.location.voiture.services.impl;


import com.location.voiture.dao.ContratDao;
import com.location.voiture.domain.EnLocationException;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.dto.RevenuData;
import com.location.voiture.models.*;
import com.location.voiture.services.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContratService implements IContratService {

    private final IPerson clientService;
    private final IClient client;
    private final IVoitureService voitureService;
    private final ContratDao contratDao;
    private final IFacture factureService;

    public ContratService(IPerson clientService, IClient client, IVoitureService voitureService, ContratDao contratDao, IFacture factureService) {
        this.clientService = clientService;
        this.client = client;
        this.voitureService = voitureService;
        this.contratDao = contratDao;
        this.factureService = factureService;
    }


    @Override
    public Contrat newContrat(long contratId,
                              long clnt1,
                              long clnt2,
                              Long voitureId,
                              int numDay, Double prix,
                              Double avance,
                              LocalDateTime depart,
                              LocalDateTime retour, boolean isPayer, double caution) throws EnLocationException, ResourceNotFoundException {


        Client client1 = client.getClient(clnt1);
        Voiture voiture = voitureService.getOne(voitureId);
        List<Contrat> byVoitureMatricule = findContratByVoitureMatricule(voiture.getMatricule());


        Contrat contrat;
        Facture facture;
        if (contratId != 0) {
            // update contrat if Contrat exist by id else add new contrat
            contrat = getOneContrat(contratId);
            byVoitureMatricule.remove(contrat);
            facture = factureService.FactureByContratId(contratId);

        } else {
            contrat = new Contrat(); // create new Contrat
            contrat.setNumContrat("C" + System.currentTimeMillis() / 1000 + "-" +
                    Long.toString(LocalDate.now().getYear()).substring(2));
            facture = new Facture();
        }
        if (retour != null)
            validatePeriod(depart, retour, byVoitureMatricule);


        contrat.setDateDepart(depart);
        contrat.setDateRetour(retour);
        contrat.setDateCreated(LocalDateTime.now());
        contrat.setTarif(prix);
        contrat.setAvance(avance);
        contrat.setNumDay(numDay);
        contrat.setCaution(caution);
        contrat.setPayer(isPayer);

        // max client in one contrat is two
        // replace client 1 if exist in contrat else add

        contrat.setDriverOne(client1);

        if (clnt2 > 0) {
            Client client2 = clientService.getPerson(clnt2);
            // replace client 2 if exist in contrat else add
            contrat.setDriverTwo(client2);
        } else if (clnt2 == 0)
            contrat.setDriverTwo(null);

       /*if(retour==null || retour.toLocalDate().isAfter(LocalDate.now()))
           voiture.setEnLocation(true);
       else voiture.setEnLocation(isEnLocation(voiture));*/

        contrat.setVoiture(voiture);
        
        if (facture != null) {
            facture.setContrat(contrat);
            factureService.saveFacture(facture);

        }
        return contratDao.save(contrat);
    }

    /*private boolean isEnLocation(Voiture voiture) {
        List<Contrat> contratByVoitureMatricule = findContratByVoitureMatricule(voiture.getMatricule());
        return contratByVoitureMatricule.stream().anyMatch(contrat ->{
            if (contrat.getDateRetour() == null)
                return true;
                   else return contrat.getDateRetour().toLocalDate().isAfter(LocalDate.now());
                });
                    }*/

    @Override
    public List<Contrat> getAllContrat() {
        return contratDao.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public List<Contrat> getContratsEncours() {
        return contratDao.getContartsEnCours();
    }
    @Override
    public  List<RemainingDaysOfContrat> getRemainingDaysOfContrat(){
        List<Object[]> remainingDaysOfContrat = contratDao.getRemainingDaysOfContrat();
              return remainingDaysOfContrat.stream().map(data-> new RemainingDaysOfContrat((int)data[0],data[1].toString())).collect(Collectors.toList());

    }

    @Override
    public List<Contrat> getAllContratByDateRetour() {
        return contratDao.findAll().stream().filter(contrat ->
                contrat.getDateRetour() != null).filter(contrat -> contrat.getDateRetour().toLocalDate().equals(LocalDate.now())).collect(Collectors.toList());
    }


    @Override
    public Contrat getOneContrat(long contratId) throws ResourceNotFoundException {
        return contratDao.findById(contratId).orElseThrow(() -> new ResourceNotFoundException("Aucun contrat trouvé pour Id : " + contratId));
    }

    @Override
    public List<Contrat> findContratByVoitureMatricule(String voitureMatricule) {
        return contratDao.findAllByVoitureMatricule(voitureMatricule);
    }

    @Override
    public List<Contrat> findContratByClientsId(long driver2, long driver1) {
        return contratDao.findByDriverOne_IdOrDriverTwo_Id(driver1, driver2, Sort.by(Sort.Direction.DESC, "id"));
    }


    @Override
    public void deleteContrat(long contratId) throws ResourceNotFoundException {
        Contrat contrat = contratDao.findById(contratId).orElseThrow(() -> new ResourceNotFoundException("Aucun contart trouvé par id " + contratId));
        Voiture voiture = voitureService.getOne(contrat.getVoiture().getId());
        if (contrat.getDateRetour().toLocalDate().isAfter(LocalDate.now()) || contrat.getDateRetour() == null)
            voiture.setEnLocation(false);
        contratDao.deleteById(contrat.getId());
    }
    @Override
    public byte[] generateContratPdf(long contratId) throws ResourceNotFoundException, JRException, FileNotFoundException {
        Contrat contrat = getOneContrat(contratId);
        Person person = clientService.getPerson(contrat.getDriverOne().getId());

        Map<String, Object> map = new HashMap<>();
        DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatterLocalDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        if (contrat.getDateRetour() != null)
            map.put("dateRetour", formatterLocalDateTime.format(contrat.getDateRetour()));
        map.put("dateDepart", formatterLocalDateTime.format(contrat.getDateDepart()));
        map.put("dateNaissance", formatterLocalDate.format(person.getDateDeNaissance()));
        map.put("cinDate", formatterLocalDate.format(person.getCin().getValableJusqa()));
        map.put("permisDate", formatterLocalDate.format(person.getPermis().getValableJusqa()));

        if (contrat.getDriverTwo() != null) {
            Person personTwo = clientService.getPerson(contrat.getDriverTwo().getId());
            map.put("dateNaissanceDriverTwo", formatterLocalDate.format(personTwo.getDateDeNaissance()));
            map.put("cinDateDriverTwo", formatterLocalDate.format(personTwo.getCin().getValableJusqa()));
            map.put("permisDateDriverTwo", formatterLocalDate.format(personTwo.getPermis().getValableJusqa()));
        }


        JRBeanArrayDataSource jrBeanArrayDataSource = new JRBeanArrayDataSource(Collections.singletonList(contrat).toArray());

        JasperReport jasperReport = JasperCompileManager.compileReport(new FileInputStream("/resources/templates/scenario-contrat.jrxml"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, jrBeanArrayDataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }


    @Override
    public List<RevenuData> getRevenu(int year, long id ){
        List<Object[]> revenuAnnuel = contratDao.getRevenuAnnuel(year, id);
        return revenuAnnuel.stream().map(objects ->
                new RevenuData((double)objects[0], (double) objects[1])).collect(Collectors.toList());
    }

    private void validatePeriod(LocalDateTime depart, LocalDateTime retour, List<Contrat> byVoitureMatricule) throws EnLocationException {
        List<Contrat> collect = byVoitureMatricule.stream().filter(contrat -> contrat.getDateRetour() != null).collect(Collectors.toList());
        for (Contrat contrat : collect) {
            if (retour.toLocalDate().compareTo(contrat.getDateDepart().toLocalDate()) > 0 && retour.toLocalDate().compareTo(contrat.getDateRetour().toLocalDate()) < 0 ||
                    depart.toLocalDate().compareTo(contrat.getDateDepart().toLocalDate()) > 0 && depart.toLocalDate().compareTo(contrat.getDateRetour().toLocalDate()) < 0 ||
                    contrat.getDateRetour().toLocalDate().compareTo(depart.toLocalDate()) > 0 && contrat.getDateRetour().toLocalDate().compareTo(retour.toLocalDate()) < 0 ||
                    contrat.getDateRetour().toLocalDate().compareTo(depart.toLocalDate()) > 0 && contrat.getDateDepart().toLocalDate().compareTo(retour.toLocalDate()) < 0)
                throw new EnLocationException("Ce véhicule a été en location durant cette période. N° contrat: " +contrat.getNumContrat());
        }
    }
}

