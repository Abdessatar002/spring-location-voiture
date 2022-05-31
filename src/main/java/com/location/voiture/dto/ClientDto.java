package com.location.voiture.dto;


import com.location.voiture.models.Cin;
import com.location.voiture.models.Permis;
import com.location.voiture.models.Person;
import lombok.Data;

@Data
public class ClientDto {
    private Person client;
    private Cin cin;
    private Permis permis;
}
