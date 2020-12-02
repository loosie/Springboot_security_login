package dotsecurity.login.controller;

import dotsecurity.login.domain.User;
import dotsecurity.login.network.Header;
import dotsecurity.login.network.request.SessionApiRequest;
import dotsecurity.login.network.request.UserApiRequest;
import dotsecurity.login.network.response.SessionApiResponse;
import dotsecurity.login.network.response.UserApiResponse;
import dotsecurity.login.security.JwtTokenProvider;
import dotsecurity.login.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;


    /**
     * 로그인
     */
    @PostMapping("/login")
    public Header<ResponseEntity<SessionApiResponse>> create(@RequestBody Header<SessionApiRequest> resource) throws URISyntaxException {

        SessionApiRequest data = resource.getData();
        String email = data.getEmail();
        String password = data.getPassword();

        //(email,password) -> (userId, name)
        User user = sessionService.authenticate(email,password);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);


        String url = "/user" + user.getId();

        return Header.OK(
                ResponseEntity.created(new URI(url)).body(
                        SessionApiResponse.builder()
                        .name(user.getName())
                        .accessToken(jwt)
                        .build())
                        );
    }


    /**
     * 로그아웃 처리
     */

}
