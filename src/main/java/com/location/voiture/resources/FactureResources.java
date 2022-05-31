package com.location.voiture.resources;


import com.location.voiture.models.Facture;
import com.location.voiture.services.IFacture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/facture")
public class FactureResources {

    @Autowired
    IFacture factureService;


    @GetMapping
    public List<Facture> allFacture(){
        return factureService.allFacture();
    }


}
