package com.location.voiture.dao;


import com.location.voiture.models.Person;
import com.location.voiture.models.SelectClient;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonDao extends JpaRepository<Person, Long> {
    Person findByCinCinId(String cimId);
    Person findByPermisPermisId(String permisId);
    @Query(value = "select * from Client c where c.nom like  %:key% or c.prenom like %:key%" ,nativeQuery = true)
    List<Person> findClientByKey(@Param("key") String key);


    @Query(value = "SELECT new com.location.voiture.models.SelectClient(id ,Concat(prenom ,' ',nom)) from Person")
    List<SelectClient> getClientsForSelect(Sort nom);



}
