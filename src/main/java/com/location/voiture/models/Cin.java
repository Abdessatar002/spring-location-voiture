package com.location.voiture.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

@Data
@Entity
public class Cin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true , nullable = false , length = 30)
    private String cinId;

    private LocalDate valableJusqa;
}
