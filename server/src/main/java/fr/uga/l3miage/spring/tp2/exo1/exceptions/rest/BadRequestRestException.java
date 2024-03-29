package fr.uga.l3miage.spring.tp2.exo1.exceptions.rest;

public class BadRequestRestException extends RuntimeException{
    public BadRequestRestException(String message) {
        super(message);
    }
}
