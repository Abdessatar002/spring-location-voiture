package com.location.voiture.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Voiture implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String matricule;

    private String marque;

    private String model;

    private String imageUrl;

    private String carburant;

    private double caution;

    private String couleur;

    private Double tarif;

    private String numChassis;

    private short numPlace;

    private String gearbox;

    private LocalDate createdAt;

    private LocalDate assurance;
    private LocalDate visiteTechnique;
    private LocalDate taxe;
    private boolean isActive;

    @Transient
    private boolean isEnLocation;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "voiture")
    private List<Document> voitureDoc;

    public void addDoc(Document document) {
        if (voitureDoc == null) {
            voitureDoc = new ArrayList<>();
        }
        voitureDoc.add(document);
        document.setVoiture(this);
    }

    @JsonIgnore
    @OneToMany(mappedBy = "voiture", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Contrat> contrat;
}

