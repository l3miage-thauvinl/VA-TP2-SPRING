package fr.uga.l3miage.spring.tp2.exo1.exceptions.technical;

public class NotFoundSongEntityException extends Exception{
    public NotFoundSongEntityException(String message) {
        super(message);
    }
}
