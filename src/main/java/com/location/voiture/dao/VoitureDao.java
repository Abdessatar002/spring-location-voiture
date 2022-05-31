package com.location.voiture.dao;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.location.voiture.models.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface VoitureDao extends JpaRepository<Voiture, Long> {

    Voiture findByMatricule(String matricule);

    @Query(value = "from Voiture where visiteTechnique < :toDayPlus7")
    List<Voiture> visiteBeforeExpire(@Param("toDayPlus7") LocalDate toDayPlus7);

    @Query(value = "from Voiture where assurance < :toDayPlus7")
    List<Voiture> assuranceBeforeExpire(@Param("toDayPlus7") LocalDate toDayPlus7);

    @Query(value = "from Voiture where taxe < :toDayPlus7")
    List<Voiture> taxeBeforeExpire(@Param("toDayPlus7") LocalDate toDayPlus7);

}
