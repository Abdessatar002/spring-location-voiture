package com.location.voiture.resources;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.location.voiture.dao.VoitureDao;
import com.location.voiture.domain.ExceptionHandling;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.domain.VoitureExistException;
import com.location.voiture.models.HttpResponse;
import com.location.voiture.models.Voiture;
import com.location.voiture.services.IVoitureService;
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


import static com.location.voiture.constant.FileConstant.FORWARD_SLASH;
import static com.location.voiture.constant.FileConstant.VOITURE_FOLDER;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

@CrossOrigin
@RestController
@RequestMapping("/voiture")
public class VoitureResources extends ExceptionHandling {
    @Autowired
    IVoitureService voitureService;

    @Autowired
    VoitureDao voitureDao;


    @GetMapping("/visite")
    public List<Voiture> visiteBeforeExpire() {
        return voitureDao.visiteBeforeExpire();
    }
    @GetMapping("/assurance")
    public List<Voiture> assuranceBeforeExpire() {
        return voitureDao.assuranceBeforeExpire();
    }
    @GetMapping("/taxe")
    public List<Voiture> taxeBeforeExpire() {
        return voitureDao.taxeBeforeExpire();
    }

    @GetMapping("/voitures_dispo")
    public List<Voiture> getVoituresDispo() {
        return voitureService.getVoituresDispo();
    }

    @GetMapping("/all")
    public List<Voiture> getVoitures(@RequestParam(required = false,value = "sortField") String sortField) {
        return voitureService.getVoitures(sortField);
    }

    @GetMapping("{id}")
    public ResponseEntity<Voiture>  getVoiture(@PathVariable("id")long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(voitureService.getOne(id), HttpStatus.OK) ;
    }

    @PostMapping("/add")
    public ResponseEntity<Voiture> addNewVoiture(@RequestParam("voiture") String voiture, @RequestParam(value = "imageUrl",required = false) MultipartFile multipartFile) throws IOException, VoitureExistException {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        Voiture voiture1 = objectMapper.readValue(voiture, Voiture.class);
        Voiture newVoiture = voitureService.addNewVoiture(voiture1, multipartFile);
        return new ResponseEntity<>(newVoiture, HttpStatus.OK);
    }
    @PutMapping("/update")
    public ResponseEntity<Voiture> updateVoiture(@RequestParam("voiture") String voiture, @RequestParam(value = "imageUrl",required = false) MultipartFile multipartFile) throws IOException, VoitureExistException, ResourceNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        Voiture voiture1 = objectMapper.readValue(voiture, Voiture.class);
        Voiture newVoiture = voitureService.updateVoiture(voiture1, multipartFile);
        return new ResponseEntity<>(newVoiture, HttpStatus.OK);
    }
    @PostMapping("/upload")
    public ResponseEntity<?>addVoitureDoc(@RequestParam("id")long id , @RequestParam("fileName") String fileName , @RequestParam("file") MultipartFile file) throws IOException {
        voitureService.addVoitureDoc(id , fileName , file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/doc/{matricule}/{fileName}" , produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("matricule") String matricule, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(VOITURE_FOLDER+ matricule+ FORWARD_SLASH+ fileName));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteVoiture(@PathVariable Long id) {
        voitureService.deleteVoiture(id);
        return response(HttpStatus.OK, "La voiture été supprimé avec succès.");
    }


    @GetMapping(path = "/image/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(VOITURE_FOLDER+  fileName));
    }


    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                    message), httpStatus);
    }
}
