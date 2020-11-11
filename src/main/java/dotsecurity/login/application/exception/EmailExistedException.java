package dotsecurity.login.application.exception;

public class EmailExistedException extends RuntimeException {
    public EmailExistedException(String email){
        super("Email is already registerd: " + email);
    }

}
