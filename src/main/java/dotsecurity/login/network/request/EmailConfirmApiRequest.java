package dotsecurity.login.network.request;

import lombok.Data;

@Data
public class EmailConfirmApiRequest {

    private String email;
    private String token;

}
