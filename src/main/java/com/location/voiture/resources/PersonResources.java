package com.location.voiture.resources;


import com.location.voiture.dao.PersonDao;
import com.location.voiture.domain.ExceptionHandling;
import com.location.voiture.domain.FileExistException;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.domain.UsernameExistException;
import com.location.voiture.dto.ClientDto;
import com.location.voiture.models.Client;
import com.location.voiture.models.HttpResponse;
import com.location.voiture.models.Person;
import com.location.voiture.models.SelectClient;
import com.location.voiture.services.IPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


import static com.location.voiture.constant.FileConstant.CLIENT_FOLDER;
import static com.location.voiture.constant.FileConstant.FORWARD_SLASH;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

@CrossOrigin
@RestController
@RequestMapping("/person")
public class PersonResources extends ExceptionHandling {
    @Autowired
    private IPerson clientService;
    @Autowired
    private PersonDao personDao;



    @GetMapping("/selectClients")
    public List<SelectClient> getClientsForSelect() {
        return clientService.getClientsForSelect();
    }
    @GetMapping("/all")
    public List<Person> getClients(@RequestParam(required = false) String sortField) {
        return clientService.getClients(sortField);
    }


    @GetMapping("/{id}")
    public Client getOneClient(@PathVariable long id) throws ResourceNotFoundException {
        return clientService.getPerson(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Client>  addNewClient(@RequestBody ClientDto clientDto) throws UsernameExistException {
        Client client = clientService.addNewPerson(clientDto);
        return new ResponseEntity<>(client,HttpStatus.OK);
    }

    @PutMapping("/update")
    public Client updateClient(@RequestBody Person person) throws UsernameExistException, ResourceNotFoundException {
        return clientService.updatePerson(person);
    }

    @PostMapping("/upload")
    public ResponseEntity<Client> uploadClientImage(@RequestParam("id") String id , @RequestParam("fileName") String fileName , @RequestParam("docUrl")MultipartFile multipartFile) throws IOException, ResourceNotFoundException, UsernameExistException, FileExistException {
        long clientId = Long.parseLong(id);
        Client client = clientService.uploadPersonImage(clientId, fileName, multipartFile);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @GetMapping("/search/{key}")
    public List<Person> findClientByKey(@PathVariable String key){
       return clientService.findPersonByKey(key);
    }

    @GetMapping(path = "/image/{cinId}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("cinId") String cinId, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(CLIENT_FOLDER+ cinId+ FORWARD_SLASH+ fileName));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteClient(@PathVariable long id){
        clientService.deletePerson(id);
        return response(HttpStatus.OK , "Client Supprimé avec succés");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
}
