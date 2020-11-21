package dotsecurity.login.service;


import dotsecurity.login.AppProperties;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

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

        createRoleCustomer(newUser.getId());

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
    public UserHasRole createRoleCustomer(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(
                        ()-> new EmailNotExistedException("userId")
                );
        Role role = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(
                        ()-> new EmailNotExistedException("roleId")
                );

        UserHasRole userHasRole = UserHasRole.builder()
                .role(role)
                .user(user)
                .build();

        return userHasRoleRepository.save(userHasRole);
    }

    public UserHasRole createRoleMember(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(
                        ()-> new EmailNotExistedException("userId")
                );
        Role role = roleRepository.findByName(RoleName.ROLE_MEMBER)
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