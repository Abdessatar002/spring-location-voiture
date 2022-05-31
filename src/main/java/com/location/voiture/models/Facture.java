package com.location.voiture.models;


import com.location.voiture.generator.FactureIdGenerator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.time.LocalDate;

@Setter
@Getter
@Entity
public class Facture {
    @Column(length = 166)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "facture_seq")
    @GenericGenerator(
            name = "facture_seq",
            strategy = "com.location.voiture.generator.FactureIdGenerator",
            parameters = {
                    @Parameter(name = FactureIdGenerator.INCREMENT_PARAM, value = "1"),
                    @Parameter(name = FactureIdGenerator.VALUE_PREFIX_PARAMETER, value =FactureIdGenerator.VALUE_PREFIX_PARAMETER ),
                    @Parameter(name = FactureIdGenerator.NUMBER_FORMAT_PARAMETER, value =FactureIdGenerator.NUMBER_FORMAT_PARAMETER )
                    })

    private String id;

    @CreationTimestamp
    private LocalDate createdAt;
    @OneToOne
    private Contrat contrat;
}
