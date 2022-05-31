package com.location.voiture.services;


import com.location.voiture.models.Facture;

import java.util.List;

public interface IFacture {


    Facture saveFacture(Facture facture);

    Facture FactureByContratId(long contratId);

    List<Facture> allFacture();
}
