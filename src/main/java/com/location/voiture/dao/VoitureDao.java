package com.location.voiture.dao;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.location.voiture.models.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoitureDao extends JpaRepository<Voiture, Long> {
    Voiture findByMatricule(String matricule);

    @Query(value = "SELECT * from voiture where visite_technique < date_add(current_date(),INTERVAL 7 DAY)", nativeQuery = true)
    List<Voiture> visiteBeforeExpire();

    @Query(value = "SELECT * from voiture where assurance < date_add(current_date(),INTERVAL 7 DAY)", nativeQuery = true)
    List<Voiture> assuranceBeforeExpire();

    @Query(value = "SELECT * from voiture where taxe < date_add(current_date(),INTERVAL 7 DAY)", nativeQuery = true)
    List<Voiture> taxeBeforeExpire();

}
