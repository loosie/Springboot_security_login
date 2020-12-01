package dotsecurity.login.service;


import dotsecurity.login.AppProperties;
import dotsecurity.login.application.exception.AuthNotAllowedException;
import dotsecurity.login.application.exception.DuplicatedException;
import dotsecurity.login.application.exception.EmailExistedException;
import dotsecurity.login.application.exception.EmailNotExistedException;
import dotsecurity.login.domain.Role;
import dotsecurity.login.domain.RoleName;
import dotsecurity.login.domain.User;
import dotsecurity.login.domain.UserHasRole;
import dotsecurity.login.domain.repository.RoleRepository;
import dotsecurity.login.domain.repository.UserHasRoleRepository;
import dotsecurity.login.domain.repository.UserRepository;
import dotsecurity.login.mail.EmailMessage;
import dotsecurity.login.mail.EmailService;
import dotsecurity.login.network.Header;
import dotsecurity.login.network.request.ArtistConfirmApiRequest;
import dotsecurity.login.network.request.UserApiRequest;
import dotsecurity.login.network.response.EmailApiResponse;
import dotsecurity.login.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserHasRoleRepository userHasRoleRepository;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    @Transactional
    public User processNewAccount(UserApiRequest user) {
        User newUser = createUser(user);

        createRoleCustomer(newUser.getId(), RoleName.ROLE_CUSTOMER);

        return newUser;
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
    public User createUser(UserApiRequest userData) {

        if(userRepository.findByEmail(userData.getEmail()).isPresent()){
            throw new EmailExistedException(userData.getEmail());
        }

        if(userRepository.findByName(userData.getName()).isPresent()){
            throw new DuplicatedException(userData.getName());
        }


        String encodedPassword = passwordEncoder.encode(userData.getPassword());

        User newUser = User.builder()
                .email(userData.getEmail())
                .name(userData.getName())
                .password(encodedPassword)
                .build();
        //이메일 인증 토큰 생성
        newUser.generateEmailCheckToken();

        return userRepository.save(newUser);
    }

    /**
     * 회원가입시 ROLE_USER 자동 부여
     */
    public UserHasRole createRoleCustomer(Long userId, RoleName roleName){
        return getUserHasRole(userId, roleName);
    }

    public UserHasRole createRoleMember(Long userId, RoleName roleName){
        return getUserHasRole(userId, roleName);
    }

    public UserHasRole createRoleArtist(Long userId, RoleName roleName){
        return getUserHasRole(userId, roleName);
    }

    private UserHasRole getUserHasRole(Long userId, RoleName roleName) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        ()-> new EmailNotExistedException("userId")
                );
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(
                        ()-> new EmailNotExistedException("roleId")
                );

        UserHasRole userHasRole = UserHasRole.builder()
                .role(role)
                .user(user)
                .build();

        return userHasRoleRepository.save(userHasRole);
    }



    public EmailApiResponse sendEmailConfirmToken(String email) {
        User newUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotExistedException(email));

        Context context = new Context();
        context.setVariable("link", "/check-email-token?token="+ newUser.getEmailCheckToken() +
                "&email=" + newUser.getEmail());
        context.setVariable("nickname", newUser.getName());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "블루닷 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newUser.getEmail())
                .subject("블루닷, 이메일 인증")
                .message(message)
                .build();


        emailService.sendEmail(emailMessage);

        return EmailApiResponse.builder()
                .message("send to :" + newUser.getEmail())
                .build();
    }


    public boolean checkIsEmailConfirmedUser(String email){

        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new EmailNotExistedException(email));

        if(user.isEmailVerified()){
            return true;
        }

        return false;
    }


    public boolean checkIsArtistProfile(ArtistConfirmApiRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("user not existed"));

        //인증된 이메일인지 확인
        if(!checkIsEmailConfirmedUser(user.getEmail())){
            throw new AuthNotAllowedException();
        }

        createRoleArtist(user.getId(), RoleName.ROLE_ARTIST);

        user.artistEnrollment();

        return true;
    }

    public EmailApiResponse checkEmailConfirm(String email, String token) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not existed"));

        if(!user.isValidToken(token)){
            throw new AuthNotAllowedException();
        }

        //-- 이메일 인증 완료 user db에 저장 --//
        user.completeEmailConfirm();

        //-- ROLE_CUSTOMER -> ROLE_MEMBER로 변경 --//
        if(!user.isEmailVerified()){
            throw new AuthNotAllowedException();
        }
        createRoleMember(user.getId(), RoleName.ROLE_MEMBER);

        return EmailApiResponse.builder()
                .message("email 인증 완료")
                .build();
    }


    public boolean emailIsExisted(String email) {
        Optional<User> existed = userRepository.findByEmail(email);

        if (existed.isPresent()) {
            return true;
        }

        return false;
    }
}