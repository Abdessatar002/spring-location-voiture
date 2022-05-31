package com.location.voiture.dao;

import com.location.voiture.models.Contrat;
import com.location.voiture.models.RemainingDaysOfContrat;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContratDao extends JpaRepository<Contrat, Long> {
    // SELECT month(contrat.date_depart) AS mon, SUM(tarif * num_day) as total FROM contrat WHERE contrat.voiture_id = 8 AND YEAR(contrat.date_depart) = 2022
// GROUP BY MONTH(contrat.date_depart)
    @Query(value = "SELECT MONTH(contrat.date_depart) AS mon, SUM(tarif * num_day) as total FROM contrat WHERE contrat.voiture_id = :id AND YEAR(contrat.date_depart) = :theYear" +
            " GROUP BY MONTH(contrat.date_depart)", nativeQuery = true)
    List<Object[]> getRevenuAnnuel(@Param("theYear") int theYear, @Param("id") Long id);


    List<Contrat> findAllByVoitureMatricule(String matricule);

    List<Contrat> findByDriverOne_IdOrDriverTwo_Id(long driverOne_id, long driverTwo_id, Sort sort);

    List<Contrat> findAllByDriverOne_PrenomContainingOrNumContratContainingOrDateCreatedLike
            (String driverOne_prenom, String numContrat, LocalDateTime dateCreated);

    @Query(value = "SELECT datediff(DATE(date_retour)," +
            "DATE(current_date())) as resulte , voiture.matricule as matricule " +
            "from contrat join voiture on voiture_id = voiture.id " +
            "where DATE(contrat.date_retour) > DATE(current_date())",
            nativeQuery = true)
    List<Object[]> getRemainingDaysOfContrat();


    @Query(value = "select * from contrat where DATE(date_retour)> DATE(current_date()) or date_retour is null ", nativeQuery = true)
    List<Contrat> getContartsEnCours();


}


