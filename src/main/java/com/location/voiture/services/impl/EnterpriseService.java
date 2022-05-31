package com.location.voiture.services.impl;


import com.location.voiture.dao.EnterpriseDao;
import com.location.voiture.domain.ResourceExistException;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.models.Enterprise;
import com.location.voiture.services.IEnterprise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnterpriseService implements IEnterprise {
    @Autowired
    private EnterpriseDao enterpriseDao;


    @Override
    public List<Enterprise> getAllEnterprise(String sortField){
            if (!sortField.equals(""))
           return enterpriseDao.findAll(Sort.by(Sort.Direction.ASC,sortField));
            else return enterpriseDao.findAll(Sort.by(Sort.Direction.DESC,"id"));

    }
    @Override
    public Enterprise addEnterprise(Enterprise enterprise){
        return enterpriseDao.save(enterprise);
    }

    @Override
    public Enterprise updateEnterprise(Enterprise enterprise) throws ResourceNotFoundException, ResourceExistException {
        Enterprise oldEnterprise = getOneEnterprise(enterprise.getId());
        oldEnterprise.setIce(enterprise.getIce());
        oldEnterprise.setPrenom(enterprise.getPrenom());
        oldEnterprise.setAddress(enterprise.getAddress());
        oldEnterprise.setTel(enterprise.getTel());
        return enterpriseDao.save(oldEnterprise);
    }

    @Override
    public Enterprise getOneEnterprise(long enterpriseId) throws ResourceNotFoundException {
       return enterpriseDao.findById(enterpriseId).orElseThrow(() -> new  ResourceNotFoundException("Aucune société trouvé par id: "+enterpriseId ));
    }

    @Override
    public void deleteEnterprise(long enterpriseId) throws ResourceNotFoundException {
        Enterprise enterprise = getOneEnterprise(enterpriseId);
        enterpriseDao.deleteById(enterprise.getId());
    }
}
