package com.location.voiture.services.impl;


import com.location.voiture.dao.FactureDao;
import com.location.voiture.models.Facture;
import com.location.voiture.services.IFacture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactureServiceImpl implements IFacture {
    @Autowired
    FactureDao factureDao;

    @Override
    public Facture saveFacture(Facture facture){
        return factureDao.save(facture);
    }
    @Override
    public Facture FactureByContratId(long contratId){
        return factureDao.findByContrat_Id(contratId);
    }

    @Override
    public List<Facture> allFacture(){
       return factureDao.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
}
