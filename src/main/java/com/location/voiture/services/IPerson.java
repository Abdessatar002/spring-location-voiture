package com.location.voiture.services;


import com.location.voiture.domain.FileExistException;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.domain.UsernameExistException;
import com.location.voiture.dto.ClientDto;
import com.location.voiture.models.Person;
import com.location.voiture.models.SelectClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface IPerson {

    List<Person> getClients(String sortField);


    List<SelectClient> getClientsForSelect();

    Person getPerson(long id) throws ResourceNotFoundException;

    Person addNewPerson(ClientDto clientDto) throws UsernameExistException;

    Person updatePerson(Person person) throws UsernameExistException, ResourceNotFoundException;

    void deletePerson(long clintId);
    List<Person> findPersonByKey(String key);

    Person uploadPersonImage(long id, String fileName, MultipartFile multipartFile) throws IOException, ResourceNotFoundException, UsernameExistException, FileExistException;


}
