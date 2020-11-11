package dotsecurity.login.network.request;

import lombok.Data;

/**
 * 로그인 요청 데이터
 */

@Data
public class SessionApiRequest {

    private String name;

    private String email;

    private String password;
}
