package com.location.voiture.dao;


import com.location.voiture.models.Permis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermisDao extends JpaRepository<Permis, Long> {
}
