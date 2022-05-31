package com.location.voiture.dao;


import com.location.voiture.models.Cin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CinDao extends JpaRepository<Cin, Long> {
}
