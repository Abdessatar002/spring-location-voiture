package com.location.voiture.resources;


import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.services.IDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/document")
public class DocumentResources {
    @Autowired
    private IDocument documentService;



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoc(@PathVariable long id) throws IOException, ResourceNotFoundException {
        documentService.deleteDoc(id);
        return  new ResponseEntity<>(HttpStatus.OK);
    }
}
