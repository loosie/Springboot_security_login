package dotsecurity.login.application.exception;


public class AuthNotAllowedException extends RuntimeException {
    public AuthNotAllowedException(){
        super("AuthN is not allowed");
    }
}
