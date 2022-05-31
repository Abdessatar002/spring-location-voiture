package com.location.voiture.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(length = 10)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String prenom;

    private String address;

    private String tel;

    @JsonIgnore
    @OneToMany(mappedBy = "driverOne",fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private List<Contrat> contrat;

    @JsonIgnore
    @OneToMany(mappedBy = "driverTwo",fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private List<Contrat> contrat1;

    @OneToMany(cascade = CascadeType.ALL,mappedBy ="client")
    private List<Document> clientDoc;

    public void add(Document document) {
        if (clientDoc == null){
            clientDoc = new ArrayList<>() ;
        }
        clientDoc.add(document);
        document.setClient(this);

}    }

