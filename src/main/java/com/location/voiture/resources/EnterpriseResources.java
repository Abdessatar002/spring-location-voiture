package com.location.voiture.resources;


import com.location.voiture.dao.EnterpriseDao;
import com.location.voiture.domain.ExceptionHandling;
import com.location.voiture.domain.ResourceExistException;
import com.location.voiture.domain.ResourceNotFoundException;
import com.location.voiture.models.Enterprise;
import com.location.voiture.services.IEnterprise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/enterprise")
public class EnterpriseResources extends ExceptionHandling {
    @Autowired
    IEnterprise enterpriseService;
    @Autowired
    private EnterpriseDao enterpriseDao;



    @GetMapping
    public ResponseEntity<List<Enterprise>>  getAllEnterprise(@RequestParam(required = false ,value ="sortField") String sortField){
       return new ResponseEntity<>(enterpriseService.getAllEnterprise(sortField), HttpStatus.OK) ;
    }

    @GetMapping("/{enterpriseId}")
    public ResponseEntity<Enterprise>  getOneEnterprise(@PathVariable long enterpriseId) throws ResourceNotFoundException {
        return new ResponseEntity<>(enterpriseService.getOneEnterprise(enterpriseId), HttpStatus.OK) ;
    }

    @PostMapping
    public ResponseEntity<Enterprise> addEnterprise(@RequestBody Enterprise enterprise) throws ResourceExistException {

        return new ResponseEntity<>(enterpriseService.addEnterprise(enterprise),HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Enterprise> updateEnterprise(@RequestBody Enterprise enterprise) throws ResourceExistException, ResourceNotFoundException {
        return new ResponseEntity<>(enterpriseService.updateEnterprise(enterprise),HttpStatus.OK);
    }
    @DeleteMapping("/{enterpriseId}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpStatus> deleteEnterprise(@PathVariable long enterpriseId) throws ResourceNotFoundException {
        enterpriseService.deleteEnterprise(enterpriseId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
