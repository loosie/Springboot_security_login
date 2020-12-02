package dotsecurity.login.application.exception;

public class ArtistNotEnrolledIdException extends RuntimeException{
    public ArtistNotEnrolledIdException() {
        super("not registered Id");
    }
}
