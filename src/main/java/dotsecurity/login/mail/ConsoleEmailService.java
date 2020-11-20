package dotsecurity.login.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Primary
//@Profile("local")
@Slf4j
@Component
public class ConsoleEmailService //implements EmailService {
{
 //   @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("sent email : {}", emailMessage.getMessage());
    }
}
