package com.location.voiture.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemainingDaysOfContrat {
    private int result;
    private String matricule;
}
