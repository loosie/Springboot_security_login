package dotsecurity.login.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User 프로필 수정 응답 데이터
 */

@Data
@Builder
public class UserProfileApiResponse {

    private String email;

    private String name;

    private String nickname;


}