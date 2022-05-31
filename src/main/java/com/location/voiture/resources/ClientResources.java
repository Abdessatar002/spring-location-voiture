package com.location.voiture.resources;


import com.location.voiture.dao.ClientDao;
import com.location.voiture.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/client")
public class ClientResources {
    @Autowired
    private ClientDao clientDao;


    @GetMapping("/{id}")

    public Client getClient(@PathVariable long id ){
        return clientDao.findById( id).get();
    }
}
