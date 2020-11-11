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


    //TODO : 반환값 token으로 변경 (created url해도 되나?)
    @PostMapping("/session")
    public ResponseEntity<SessionApiResponse> create(@RequestBody SessionApiRequest resource) throws URISyntaxException {


        String email = resource.getEmail();
        String password = resource.getPassword();

        //(email,password) -> (userId, name)
        User user = sessionService.authenticate(email,password);
//      String accessToken = jwtUtil.createToken(user.getId(),user.getName());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);


        String url = "/session";

        return ResponseEntity.created(new URI(url)).body(
                SessionApiResponse.builder()
                        .name(user.getName())
                        .accessToken(jwt)
                        .build());
    }

}
