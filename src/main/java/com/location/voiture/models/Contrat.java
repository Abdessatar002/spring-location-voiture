package com.location.voiture.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class Contrat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true , nullable = false)
    private String numContrat;
    private LocalDateTime dateCreated;
    private Double tarif;
    private Double avance;
    private int numDay;
    private LocalDateTime DateDepart;
    private double caution;
    private boolean isPayer;


    private LocalDateTime DateRetour;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private Client driverOne;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private Client driverTwo;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private Voiture voiture;




    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL,mappedBy = "contrat")
    private Facture facture;


}
