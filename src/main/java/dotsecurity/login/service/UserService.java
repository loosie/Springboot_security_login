package dotsecurity.login.service;


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
import dotsecurity.login.network.request.UserApiRequest;
import dotsecurity.login.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private EmailService emailService;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserHasRoleRepository userHasRoleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @Transactional
    public User processNewAccount(UserApiRequest user) {
        User newUser = createUser(user);

        createUserRole(newUser.getId());


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

    public void sendEmailConfirmToken(User newUser) {

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newUser.getEmail())
                .subject("닷 스터디, 이메일 인증")
                .message("/check-email-token?token="+ newUser.getEmailCheckToken() +
                        "&email=" + newUser.getEmail())
                .build();


        emailService.sendEmail(emailMessage);
    }

    public void sendLink(User user){
        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject("닷 스터디, 로그인 링크")
                .message("/email-confirm?token="+user.getEmailCheckToken() +
                        "&email=" + user.getEmail())
                .build();

        emailService.sendEmail(emailMessage);

    }


}