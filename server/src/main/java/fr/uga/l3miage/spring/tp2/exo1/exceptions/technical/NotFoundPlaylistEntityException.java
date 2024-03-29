package fr.uga.l3miage.spring.tp2.exo1.exceptions.technical;

public class NotFoundPlaylistEntityException extends Exception {
    public NotFoundPlaylistEntityException(String format) {
        super(format);
    }
}
