package com.location.voiture.services;



import com.location.voiture.domain.EnLocationException;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.dto.RevenuData;
import com.location.voiture.models.Contrat;
import com.location.voiture.models.RemainingDaysOfContrat;

import java.time.LocalDateTime;
import java.util.List;

public interface IContratService {




    Contrat newContrat(long contratId, long clnt1, long clnt2, Long voitureId, int numDay, Double prix,
                       Double avance, LocalDateTime depart, LocalDateTime retour, boolean isPayer, double caution) throws EnLocationException, ResourceNotFoundException;

    List<Contrat> getAllContrat();

    List<Contrat> getContratsEncours();

    List<RemainingDaysOfContrat> getRemainingDaysOfContrat();

    List<Contrat> getAllContratByDateRetour();

    Contrat getOneContrat(long contratId) throws ResourceNotFoundException;

    List<Contrat> findContratByVoitureMatricule(String voitureMatricule);


    List<Contrat> findContratByClientsId(long driver2, long driver1);

    void deleteContrat(long contratId) throws ResourceNotFoundException;

    List<RevenuData> getRevenu(int year, long id);
}
