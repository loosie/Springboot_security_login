package dotsecurity.login.service;

import dotsecurity.login.application.exception.EmailNotExistedException;
import dotsecurity.login.application.exception.PasswordWrongException;
import dotsecurity.login.domain.User;
import dotsecurity.login.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SessionService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SessionService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * 인증
     * (email,password) -> (userId, name)제공
     */
    public User authenticate(String email, String password) {

        //이메일 존재하지 않으면 예외처리
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotExistedException(email));

        //패스워드 예외처리
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new PasswordWrongException();
        }
        return user;
    }



}
