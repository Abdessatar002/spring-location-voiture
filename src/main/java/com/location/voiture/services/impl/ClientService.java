package com.location.voiture.services.impl;


import com.location.voiture.dao.ClientDao;
import com.location.voiture.models.Client;
import com.location.voiture.services.IClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService implements IClient {
    @Autowired
    ClientDao clientDao;



     @Override
    public Client getClient(long id){
       return clientDao.findById(id).get();
    }

}
