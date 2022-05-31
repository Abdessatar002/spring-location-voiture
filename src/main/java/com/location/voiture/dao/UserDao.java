package com.location.voiture.dao;


import com.location.voiture.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    User findByUsername(String userName);
    User findByEmail(String email);
}
