package com.location.voiture.dao;


import com.location.voiture.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDao extends JpaRepository<Client, Long> {
}
