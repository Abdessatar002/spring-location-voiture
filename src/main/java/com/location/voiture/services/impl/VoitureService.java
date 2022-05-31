package com.location.voiture.services.impl;


import com.location.voiture.dao.VoitureDao;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.domain.VoitureExistException;
import com.location.voiture.models.Contrat;
import com.location.voiture.models.Document;
import com.location.voiture.models.Voiture;
import com.location.voiture.services.IContratService;
import com.location.voiture.services.IVoitureService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.location.voiture.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional
@CrossOrigin
public class VoitureService implements IVoitureService {

    private final VoitureDao voitureDao;

    private  IContratService contratService;



    public VoitureService(VoitureDao voitureDao,@Lazy IContratService contratService) {
        this.voitureDao = voitureDao;
        this.contratService = contratService;
    }


    @Override
    public List<Voiture> getVoitures(String sortField) {
        if (!sortField.equals(""))
            return voitureDao.findAll(Sort.by(Sort.Direction.ASC, sortField));
        else return voitureDao.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public List<Voiture> visiteBeforeExpire() {
      return   voitureDao.visiteBeforeExpire();
    }

    @Override
    public List<Voiture> assuranceBeforeExpire() {
        return   voitureDao.assuranceBeforeExpire();
    }

    @Override
    public List<Voiture> taxeBeforeExpire() {
        return   voitureDao.taxeBeforeExpire();
    }

    @Override
    public List<Voiture> getVoituresDispo(){
       return voitureDao.findAll().stream().filter(voiture -> {
            List<Contrat> contratByVoitureMatricule = contratService.findContratByVoitureMatricule(voiture.getMatricule());

            contratByVoitureMatricule.stream().anyMatch(contrat ->{
                 if (contrat.getDateRetour() == null || contrat.getDateRetour().toLocalDate().isAfter(LocalDate.now())){
                     voiture.setEnLocation(true);
                 }
                 else {
                     voiture.setEnLocation(false);
                 }
                return true;
            });

           return !voiture.isEnLocation() && voiture.isActive();
       }).collect(Collectors.toList());

    }


    private void saveVoitureImage(Voiture voiture, MultipartFile voitureImage) throws IOException {
        if (voitureImage != null) {
            Path voitureFolder = Paths.get(VOITURE_FOLDER).toAbsolutePath().normalize();
            if (!Files.exists(voitureFolder)) {
                Files.createDirectories(voitureFolder);
            }
            Files.deleteIfExists(Paths.get(voitureFolder + voiture.getMatricule() + "." + "jpg"));
            Files.copy(voitureImage.getInputStream(), voitureFolder.resolve(voiture.getMatricule() + "." + "jpg"), REPLACE_EXISTING);
            voiture.setImageUrl(setImageUrl(voiture.getMatricule()));
            voitureDao.save(voiture);
        }


    }

    private String setImageUrl(String matricule) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/voiture/image/" + matricule + "." + "jpg").toUriString();
    }


    private void validateVoiture(String matricule, long id) throws VoitureExistException {
        Voiture byMatricule = voitureDao.findByMatricule(matricule);
        if (voitureDao.findById(id).isPresent()) {
            Voiture voitureById = voitureDao.findById(id).get();
            if (byMatricule != null) {
                if (!byMatricule.getId().equals(voitureById.getId())) {
                    throw new VoitureExistException("Voiture déjà exist.");
                }
            }
        }
        if (!voitureDao.findById(id).isPresent() && byMatricule != null) {
            throw new VoitureExistException("Voiture déjà exist.");
        }
    }

    @Override
    public Voiture updateVoiture(Voiture voiture, MultipartFile multipartFile) throws ResourceNotFoundException, VoitureExistException, IOException {

        if (!voitureDao.findById(voiture.getId()).isPresent()) {
            throw new ResourceNotFoundException("Resource Not found.");
        }
        Voiture oldVoiture = voitureDao.findById(voiture.getId()).get();

        validateVoiture(voiture.getMatricule(), voiture.getId());
        oldVoiture.setMarque(voiture.getMarque());
        oldVoiture.setModel(voiture.getModel());
        oldVoiture.setMatricule(voiture.getMatricule());
        oldVoiture.setCarburant(voiture.getCarburant());
        oldVoiture.setCouleur(voiture.getCouleur());
        oldVoiture.setTarif(voiture.getTarif());
        oldVoiture.setNumPlace(voiture.getNumPlace());
        oldVoiture.setNumChassis(voiture.getNumChassis());
        oldVoiture.setCaution(voiture.getCaution());

        oldVoiture.setAssurance(voiture.getAssurance());
        oldVoiture.setTaxe(voiture.getTaxe());
        oldVoiture.setVisiteTechnique(voiture.getVisiteTechnique());
//
        oldVoiture.setActive(voiture.isActive());
        if (multipartFile != null) {
            saveVoitureImage(oldVoiture, multipartFile);
        }
        return voitureDao.save(oldVoiture);
    }

    @Override
    public Voiture getOne(long id) throws ResourceNotFoundException {
        return voitureDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aucune vihécule trouvé avec id :" + id));
    }

    @Override
    public void addVoitureDoc(long id, String fileName, MultipartFile multipartFile) throws IOException {
        Voiture voiture = voitureDao.findById(id).get();
        Path voitureFolder = Paths.get(VOITURE_FOLDER + voiture.getMatricule()).toAbsolutePath().normalize();
        if (!Files.exists(voitureFolder)) {
            Files.createDirectories(voitureFolder);
        }
        Files.copy(multipartFile.getInputStream(), voitureFolder.resolve(fileName + DOT + JPG_EXTENSION), REPLACE_EXISTING);

        String uriString = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(VOITURE_DOC_PATH + voiture.getMatricule() + FORWARD_SLASH + fileName + DOT + JPG_EXTENSION).toUriString();

        Document doc = new Document();
        doc.setName(fileName);
        doc.setCreatedAt(new Date());
        doc.setDocUrl(uriString);
        voiture.addDoc(doc);


    }

    @Override
    public void deleteVoiture(Long id) {
        voitureDao.deleteById(id);
    }

    @Override
    public Voiture addNewVoiture(Voiture voiture, MultipartFile multipartFile) throws VoitureExistException, IOException {
        validateVoiture(voiture.getMatricule(), 0);
        voiture.setActive(true);
        voiture.setCreatedAt(LocalDate.now());
            saveVoitureImage(voiture, multipartFile);
        return voitureDao.save(voiture);
    }
}
