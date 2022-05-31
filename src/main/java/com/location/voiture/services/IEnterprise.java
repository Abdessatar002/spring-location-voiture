package com.location.voiture.services;



import com.location.voiture.domain.ResourceExistException;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.models.Enterprise;

import java.util.List;

public interface IEnterprise {
    List<Enterprise> getAllEnterprise(String sortField);

    Enterprise addEnterprise(Enterprise enterprise) throws ResourceExistException;

    Enterprise updateEnterprise(Enterprise enterprise) throws ResourceNotFoundException, ResourceExistException;

    Enterprise getOneEnterprise(long enterpriseId) throws ResourceNotFoundException;

    void deleteEnterprise(long enterpriseId) throws ResourceNotFoundException;
}
