package com.location.voiture.services;


import com.location.voiture.domain.ResourceNotFoundException;

import java.io.IOException;

public interface IDocument {

    void deleteDoc(long idDoc) throws IOException, ResourceNotFoundException;
}
