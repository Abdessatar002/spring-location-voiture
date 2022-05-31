package com.location.voiture.models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Setter
@Getter
@Entity
@DiscriminatorValue("ENTERPRISE")
public class Enterprise extends Client {
   private String ice;

}
