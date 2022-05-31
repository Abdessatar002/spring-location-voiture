package com.location.voiture.dao;


import com.location.voiture.models.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseDao extends JpaRepository<Enterprise, Long> {
    Enterprise findByIce(String ice);
}
