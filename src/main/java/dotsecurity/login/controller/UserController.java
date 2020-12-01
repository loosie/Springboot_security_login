package dotsecurity.login.controller;

import dotsecurity.login.application.exception.EmailExistedException;
import dotsecurity.login.domain.RoleName;
import dotsecurity.login.domain.User;
import dotsecurity.login.domain.repository.UserRepository;
import dotsecurity.login.network.Header;
import dotsecurity.login.network.request.EmailConfirmApiRequest;
import dotsecurity.login.network.request.SessionApiRequest;
import dotsecurity.login.network.request.UserApiRequest;
import dotsecurity.login.network.response.EmailApiResponse;
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
    public boolean email(@RequestBody SessionApiRequest request) throws URISyntaxException {
        String userEmail = request.getEmail();

        // 이메일 존재하면 true
        if(userService.emailIsExisted(userEmail)) {
            log.info("email-check : " + request.getEmail());
            return true;
        }

        // 이메일 존재x false;
        return false;

    }

    @PostMapping("/signup")
    public Header<UserApiResponse> create(@Valid @RequestBody Header<UserApiRequest> request) {
        UserApiRequest userData = request.getData();

        User returnData = userService.processNewAccount(userData);

        return Header.OK(UserApiResponse.builder()
                .id(returnData.getId())
                .email(returnData.getEmail())
                .name(returnData.getName())
                .password(returnData.getPassword())
                .build());
    }


    /**
     * email 인증 -> 토큰 발생 및 이메일로 전송
     */
    @PostMapping("/send-email-token")
    public String sendEmailToken(@RequestBody EmailConfirmApiRequest request){

        EmailApiResponse res = userService.sendEmailConfirmToken(request.getEmail());

        log.info("send to :" + request.getEmail());

        return res.getMessage();
    }

    /**
     * email 인증 처리 (token, email)
     */
    @Transactional
    @GetMapping("/check-email-token")
    public String checkEmailToken(EmailConfirmApiRequest request){

        EmailApiResponse res = userService.checkEmailConfirm(request.getEmail(), request.getToken());

        log.info("email 인증 완료");

        return res.getMessage();
    }



}
