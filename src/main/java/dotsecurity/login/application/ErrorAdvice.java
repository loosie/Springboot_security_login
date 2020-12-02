package dotsecurity.login.application;


import dotsecurity.login.application.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorAdvice {

    /**
     * 회원가입 : 이메일이 이미 존재할 때 예외처리
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailExistedException.class)
    public String handelEmailExisted(){
        return "{Email is already registerd}";
    }


    /**
     * 로그인 세션 : 이메일이 존재하지 않을 때 예외처리
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailNotExistedException.class)
    public String handleEmailNotExisted(){
        return "{}";
    }

    /**
     * 로그인 세션 : 패스워드 틀렸을 때 예외처리
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordWrongException.class)
    public String handlePasswordWrong(){
        return "{}";
    }

    /**
     * 중복 예외처리
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicatedException.class)
    public String handleDuplicatedException(){
        return "Duplicated Exception ";
    }



    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthNotAllowedException.class)
    public String handleAuthenticatedException(){
        return "authN Exception";
    }




}
