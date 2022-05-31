package com.location.voiture.domain;

public class FileExistException extends Exception {
    public FileExistException(String message) {
        super(message);
    }
}
