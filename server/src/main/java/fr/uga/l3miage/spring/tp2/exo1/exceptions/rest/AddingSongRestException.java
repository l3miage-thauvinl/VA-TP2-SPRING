package fr.uga.l3miage.spring.tp2.exo1.exceptions.rest;

public class AddingSongRestException extends RuntimeException {
    public AddingSongRestException(String message) {
        super(message);
    }
}
