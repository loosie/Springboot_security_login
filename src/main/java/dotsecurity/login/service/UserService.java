package dotsecurity.login.service;


import dotsecurity.login.application.exception.EmailExistedException;
import dotsecurity.login.domain.User;
import dotsecurity.login.domain.repository.UserRepository;
import dotsecurity.login.network.Header;
import dotsecurity.login.network.request.UserApiRequest;
import dotsecurity.login.network.response.UserApiResponse;
import dotsecurity.login.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email"));

        log.info("loadUserByUsername : " + user);


        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with id : " +id));

        return UserPrincipal.create(user);
    }

    /**
     * 회원가입 register User
     */
//    public Header<UserApiResponse> create(Header<UserApiRequest> request) {
//        UserApiRequest userData = request.getData();
//
//        Optional<User> existed = userRepository.findByEmail(userData.getEmail());
//
//        //존재하는 이메일 예외처리
//        if (existed.isPresent()) {
//            throw new EmailExistedException(userData.getEmail());
//        }
//
//        String encodedPassword = passwordEncoder.encode(userData.getPassword());
//
//        User newUser = User.builder()
//                .email(userData.getEmail())
//                .password(encodedPassword)
//                .name(userData.getName())
//                .build();
//
//        User returnData = userRepository.save(newUser);
//
//        return Header.OK(response(returnData));
//    }

    /**
     * email 페이지
     */
    public Header<UserApiResponse> checkEmail(Header<UserApiRequest> request) {
        UserApiRequest userData = request.getData();

        Optional<User> existed = userRepository.findByEmail(userData.getEmail());
        //존재하는 이메일 예외처리
        if (existed.isPresent()) {
            throw new EmailExistedException(userData.getEmail());
        }

        User newUser = User.builder()
                .email(userData.getEmail())
                .build();

        User returnData = userRepository.save(newUser);

        return Header.OK(response(returnData));
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