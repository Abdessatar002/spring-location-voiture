package com.location.voiture.domain;

public class NotAnImageFileException extends Exception {
    public NotAnImageFileException(String message) {
        super(message);
    }
}