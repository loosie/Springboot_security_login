package dotsecurity.login.application.exception;

public class DuplicatedException extends RuntimeException {
    public DuplicatedException(String source){
        super(source+ " is already existed" );
    }

}
