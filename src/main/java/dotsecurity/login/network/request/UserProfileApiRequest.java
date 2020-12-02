package dotsecurity.login.network.request;

import dotsecurity.login.domain.UserHasRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User 프로필 수정 요청 데이터
 */

@Data
@Builder
public class UserProfileApiRequest {

    private String email;

    private String name;

    private String nickname;

    //artist
    private String artistName;

    private String description;

    private String profileImg;


}