package dotsecurity.login.controller;

import dotsecurity.login.application.exception.EmailExistedException;
import dotsecurity.login.domain.RoleName;
import dotsecurity.login.domain.User;
import dotsecurity.login.domain.repository.UserRepository;
import dotsecurity.login.network.Header;
import dotsecurity.login.network.request.EmailConfirmApiRequest;
import dotsecurity.login.network.request.UserApiRequest;
import dotsecurity.login.network.response.UserApiResponse;
import dotsecurity.login.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.Optional;


@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("")
    @Secured("ROLE_CUSTOMER")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String home() {
        return "hi hello gonizziwa";
    }

    @PostMapping("/email")
    public boolean email(@RequestBody Header<UserApiRequest> request) throws URISyntaxException {
        UserApiRequest userData = request.getData();
        Optional<User> existed = userRepository.findByEmail(userData.getEmail());

        if (existed.isPresent()) {
            return true;
        }

        return false;

    }

    @PostMapping("/signup")
    public Header<UserApiResponse> create(@Valid @RequestBody Header<UserApiRequest> request) {
        UserApiRequest userData = request.getData();

        Optional<User> existed = userRepository.findByEmail(userData.getEmail());

        //존재하는 이메일 예외처리
        if (existed.isPresent()) {
            throw new EmailExistedException(userData.getEmail());
        }

        User returnData = userService.processNewAccount(userData);

        return Header.OK(response(returnData));
    }


    /**
     * email 인증 -> 토큰 발생 및 이메일로 전송
     */
    @PostMapping("/send-email-token")
    public String sendEmailToken(@RequestBody EmailConfirmApiRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email"));


        userService.sendEmailConfirmToken(user);

        String message = "send to :" + request.getEmail();
        return message;
    }

    /**
     * email 인증 처리 (token, email)
     */
    @Transactional
    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email") );


        if(!user.getEmailCheckToken().equals(token)){
            return "wrong token";
        }
        user.completeEmailConfirm();

        if(user.isEmailVerified()){
            userService.createRoleMember(user.getId(), RoleName.ROLE_MEMBER);
        }

        return "complete Email Confirm";
    }




    public UserApiResponse response(User user) {

        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .build();


        return userApiResponse;
    }


}
