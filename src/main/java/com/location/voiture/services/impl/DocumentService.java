package com.location.voiture.services.impl;


import com.location.voiture.dao.DocumentDao;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.models.Document;
import com.location.voiture.models.Person;
import com.location.voiture.services.IDocument;
import com.location.voiture.services.IPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import static com.location.voiture.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class DocumentService implements IDocument {
    @Autowired
    DocumentDao documentDao;

    @Autowired
    IPerson personService;

    @Override
    public void deleteDoc(long idDoc) throws IOException, ResourceNotFoundException {
        Document d = documentDao.findById(idDoc).orElseThrow(() -> new ResourceNotFoundException("Aucun document trouvé avec id :" + idDoc));
        if (d.getClient() != null) {
            Person person = personService.getPerson(d.getClient().getId());
            Path clientFolder = Paths.get(CLIENT_FOLDER + person.getNom() + "_" + person.getCin().getCinId() + FORWARD_SLASH + d.getName() + ".jpg");
            Files.deleteIfExists(clientFolder);
        }
        if (d.getVoiture() != null) {
            Path voitureFolder = Paths.get(VOITURE_FOLDER + d.getVoiture().getMatricule() + FORWARD_SLASH + d.getName() + DOT + JPG_EXTENSION);
            Files.deleteIfExists(voitureFolder);
        }


        documentDao.deleteById(idDoc);
    }


    private String setDocUrl(MultipartFile multipartFile, String nom, String cinId, String fileName) throws IOException {
        Path clientFolder = Paths.get(CLIENT_FOLDER + nom + "_" + cinId);

        Files.copy(multipartFile.getInputStream(), clientFolder.resolve(fileName + DOT + JPG_EXTENSION), REPLACE_EXISTING);

        return ServletUriComponentsBuilder.fromCurrentContextPath().path(clientFolder + FORWARD_SLASH + fileName + DOT + JPG_EXTENSION).toUriString();
    }
}
