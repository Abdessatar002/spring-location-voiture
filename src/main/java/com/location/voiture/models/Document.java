package com.location.voiture.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class Document implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
     private long id;
     private String name;
     private String docUrl;
     @CreationTimestamp
     private Date CreatedAt;
     @ManyToOne
     @JsonIgnore
     private Client client;

 @ManyToOne(fetch = FetchType.LAZY)
 @JsonIgnore
 private Voiture voiture;

}
