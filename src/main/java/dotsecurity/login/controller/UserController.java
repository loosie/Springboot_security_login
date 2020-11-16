package dotsecurity.login.controller;

import dotsecurity.login.application.AppException;
import dotsecurity.login.application.exception.EmailExistedException;
import dotsecurity.login.application.exception.EmailNotExistedException;
import dotsecurity.login.domain.Role;
import dotsecurity.login.domain.RoleName;
import dotsecurity.login.domain.User;
import dotsecurity.login.domain.UserHasRole;
import dotsecurity.login.domain.repository.RoleRepository;
import dotsecurity.login.domain.repository.UserHasRoleRepository;
import dotsecurity.login.domain.repository.UserRepository;
import dotsecurity.login.network.Header;
import dotsecurity.login.network.request.UserApiRequest;
import dotsecurity.login.network.response.UserApiResponse;
import dotsecurity.login.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Optional;


@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserHasRoleRepository userHasRoleRepository;

    @GetMapping("")
    @Secured("ROLE_USER")
    @PreAuthorize("hasRole('USER')")
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

        userService.checkEmail(request);
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

        String encodedPassword = passwordEncoder.encode(userData.getPassword());



        User newUser = User.builder()
                .email(userData.getEmail())
                .name(userData.getName())
                .password(encodedPassword)
                .build();


        User returnData1 = userRepository.save(newUser);

        createUserRole(returnData1.getId());


        return Header.OK(response(returnData1));
    }


    public UserHasRole createUserRole(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(
                        ()-> new EmailNotExistedException("userId")
                );
        Role role = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(
                        ()-> new EmailNotExistedException("roleId")
                );

        UserHasRole userHasRole = UserHasRole.builder()
                    .role(role)
                    .user(user)
                    .build();

        return userHasRoleRepository.save(userHasRole);
    }


    public UserApiResponse response(User user) {

        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .roleList(user.getUserRoles())
                .build();


        return userApiResponse;
    }


}
