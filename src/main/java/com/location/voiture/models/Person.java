package com.location.voiture.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("PERSON")
public class Person extends Client{
    private String nom;
    private LocalDate dateDeNaissance;

    @OneToOne(cascade = CascadeType.ALL)
    private Cin cin;

    @OneToOne(cascade = CascadeType.ALL)

    private Permis permis;



}
