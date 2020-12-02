package dotsecurity.login.controller;

import dotsecurity.login.domain.User;
import dotsecurity.login.domain.repository.UserRepository;
import dotsecurity.login.network.Header;
import dotsecurity.login.network.request.EmailConfirmApiRequest;
import dotsecurity.login.network.request.SessionApiRequest;
import dotsecurity.login.network.request.UserApiRequest;
import dotsecurity.login.network.response.ConfirmApiResponse;
import dotsecurity.login.network.response.EmailApiResponse;
import dotsecurity.login.network.response.SessionApiResponse;
import dotsecurity.login.network.response.UserApiResponse;
import dotsecurity.login.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URISyntaxException;


@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;



    @GetMapping("")
    @Secured("ROLE_CUSTOMER")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String home() {
        return "hi hello gonizziwa";
    }

    @PostMapping("/email")
    public Header<EmailApiResponse> email(@RequestBody Header<SessionApiRequest> request) throws URISyntaxException {

        SessionApiRequest data = request.getData();

        // 이메일 존재하면 true
        if(userService.emailIsExisted(data.getEmail())) {
            log.info("existed email : " + data.getEmail());

            return Header.OK(EmailApiResponse.builder()
                            .check(false)
                            .message("go to login")
                            .build());
        }

        // 이메일 존재x false;
        return Header.OK(EmailApiResponse.builder()
                .check(true)
                .message("go to signup")
                .build());

    }

    /**
     * 유저 회원가입
     * 이메일 중복 x
     * 닉네임 중복 x
     */
    @PostMapping("/signup")
    public Header<UserApiResponse> create(@Valid @RequestBody Header<UserApiRequest> request) {
        UserApiRequest userData = request.getData();

        User returnData = userService.processNewAccount(userData);

        return Header.OK(UserApiResponse.builder()
                .id(returnData.getId())
                .email(returnData.getEmail())
                .name(returnData.getName())
                .nickname(returnData.getNickname())
                .password(returnData.getPassword())
                .build());
    }


    /**
     * email 인증 -> 토큰 발생 및 이메일로 전송
     */
    @PostMapping("/send-email-token")
    public Header<ConfirmApiResponse> sendEmailToken(@RequestBody Header<EmailConfirmApiRequest> request){

        EmailConfirmApiRequest data = request.getData();

        EmailApiResponse res = userService.sendEmailConfirmToken(data.getEmail());

        return Header.OK(ConfirmApiResponse.builder()
                .message(res.getMessage())
                .build());
    }

    /**
     * email 인증 처리 (token, email)
     */
    @Transactional
    @GetMapping("/check-email-token")
    public Header<ConfirmApiResponse> checkEmailToken(EmailConfirmApiRequest request){

        EmailApiResponse res = userService.checkEmailConfirm(request.getEmail(), request.getToken());

        log.info("email 인증 완료");

        return Header.OK(ConfirmApiResponse.builder()
                .message(res.getMessage())
                .build());
    }



}
