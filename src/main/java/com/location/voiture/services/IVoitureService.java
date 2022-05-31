package com.location.voiture.services;


import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.domain.VoitureExistException;
import com.location.voiture.models.Voiture;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IVoitureService {
    List<Voiture> getVoitures(String sortField);
    Voiture addNewVoiture(Voiture voiture, MultipartFile multipartFile) throws IOException, VoitureExistException;


    List<Voiture> visiteBeforeExpire();

    List<Voiture> assuranceBeforeExpire();

    List<Voiture> taxeBeforeExpire();


    List<Voiture> getVoituresDispo();

    Voiture updateVoiture(Voiture voiture, MultipartFile multipartFile) throws ResourceNotFoundException, VoitureExistException, IOException;
    Voiture getOne(long id) throws ResourceNotFoundException;
    void addVoitureDoc(long id, String fileName, MultipartFile multipartFile) throws IOException;
    void deleteVoiture(Long id);
}
