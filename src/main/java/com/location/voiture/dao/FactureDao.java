package com.location.voiture.dao;


import com.location.voiture.models.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactureDao extends JpaRepository<Facture, String> {

    Facture findByContrat_Id(long contratId);
}
