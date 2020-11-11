package dotsecurity.login.network.response;

import lombok.Builder;
import lombok.Data;

/**
 * 로그인 인증후 제공 데이터
 */


@Data
@Builder
public class SessionApiResponse {

    private String name;
    private String accessToken;

}
