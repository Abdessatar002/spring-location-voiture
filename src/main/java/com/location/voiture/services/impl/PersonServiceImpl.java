package com.location.voiture.services.impl;


import com.location.voiture.dao.PersonDao;
import com.location.voiture.domain.FileExistException;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.domain.UsernameExistException;
import com.location.voiture.dto.ClientDto;
import com.location.voiture.models.Document;
import com.location.voiture.models.Person;
import com.location.voiture.models.SelectClient;
import com.location.voiture.services.IPerson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;


import static com.location.voiture.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional
public class PersonServiceImpl implements IPerson {

    @Autowired
    private PersonDao personDao;



    @Override
    public List<Person> getClients(String sortField) {

        if (!sortField.equals(""))
            return personDao.findAll(Sort.by(Sort.Direction.ASC,sortField));
        else return personDao.findAll(Sort.by(Sort.Direction.DESC,"id"));

    }

    @Override
    public List<SelectClient> getClientsForSelect(){
       return personDao.getClientsForSelect(Sort.by("prenom"));
    }



    @Override
    public Person getPerson(long id) throws ResourceNotFoundException {

        return personDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found."));

    }

    @Override
    public Person addNewPerson(ClientDto clientDto) throws UsernameExistException {
        if (personDao.findByCinCinId(clientDto.getCin().getCinId()) != null) {
            throw new UsernameExistException("N° C.I.N / Passport est déjà exist");
        }
        if (personDao.findByPermisPermisId(clientDto.getPermis().getPermisId()) != null) {
            throw new UsernameExistException("N° Permis est déjà exist");
        }
        Person client = clientDto.getClient();
        client.setCin(clientDto.getCin());
        client.setPermis(clientDto.getPermis());
        return personDao.save(client);
    }


    @Override
    public Person updatePerson(Person client) throws UsernameExistException, ResourceNotFoundException {

        Person oldClient = personDao.findById(client.getId()).orElseThrow(() -> new ResourceNotFoundException("Introuvable client ! "));
        String cinId = client.getCin().getCinId();
        String permisId = client.getPermis().getPermisId();

        Person byCinCinId = personDao.findByCinCinId(cinId);
        Person byPermisId = personDao.findByPermisPermisId(permisId);

        if (!StringUtils.isNotBlank(cinId)){
            throw new UsernameExistException("N° C.I.N / Passport champs obligatoire");
        }if (!StringUtils.isNoneEmpty(permisId)){
            throw new UsernameExistException("N° Permis champs obligatoire");
        }


        if (byCinCinId !=null &&!oldClient.getId().equals(byCinCinId.getId())){
            throw new UsernameExistException("N° C.I.N / Passport est déjà exist");
        }

        if (byPermisId !=null &&!oldClient.getId().equals(byPermisId.getId())){
            throw new UsernameExistException("N° Permis est déjà exist");
        }



        // delete spaces
        oldClient.getCin().setCinId(client.getCin().getCinId().replaceAll("\\s",""));
        oldClient.getCin().setValableJusqa(client.getCin().getValableJusqa());

        // delete spaces
        oldClient.getPermis().setPermisId(client.getPermis().getPermisId().replaceAll("\\s",""));
        oldClient.getPermis().setValableJusqa(client.getPermis().getValableJusqa());

            oldClient.setNom(client.getNom());
            oldClient.setPrenom(client.getPrenom());
            oldClient.setAddress(client.getAddress());
            oldClient.setTel(client.getTel());
            oldClient.setDateDeNaissance(client.getDateDeNaissance());

            personDao.save(oldClient);


        return oldClient;

    }

    @Override
    public void deletePerson(long clintId) {
        personDao.deleteById(clintId);
    }

    @Override
    public List<Person> findPersonByKey(String key) {
        return personDao.findClientByKey(key);
    }


    public Person uploadPersonImage(long id, String fileName, MultipartFile multipartFile) throws IOException, ResourceNotFoundException, UsernameExistException, FileExistException {
      Person person = getPerson(id);

        if (!StringUtils.isNotBlank(fileName) || fileName.equals("null")) {
            int i = Objects.requireNonNull(multipartFile.getOriginalFilename()).indexOf('.');
            fileName = multipartFile.getOriginalFilename().substring(0, i);
        }
        for (Document doc : person.getClientDoc()) {
            if (doc.getName().equals(fileName)) {
                throw new FileExistException("Nom de fichier déja exist");
            }
        }
        Path clientFolder = Paths.get(CLIENT_FOLDER + person.getCin().getCinId()).toAbsolutePath().normalize();
        if (!Files.exists(clientFolder)) {
            Files.createDirectories(clientFolder);
        }

        Files.copy(multipartFile.getInputStream(), clientFolder.resolve(fileName + DOT + JPG_EXTENSION), REPLACE_EXISTING);

        String uriString = ServletUriComponentsBuilder.fromCurrentContextPath().path(CLIENT_IMAGE_PATH + person.getCin().getCinId() + FORWARD_SLASH
                + fileName + DOT + JPG_EXTENSION).toUriString();


        Document document = new Document();

        document.setName(fileName);
        document.setDocUrl(uriString);
        person.add(document);
        return personDao.save(person);

    }


}


