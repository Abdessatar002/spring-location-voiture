package com.location.voiture.dao;


import com.location.voiture.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentDao extends JpaRepository<Document, Long> {
}
