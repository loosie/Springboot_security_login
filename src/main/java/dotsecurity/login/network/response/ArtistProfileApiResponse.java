package dotsecurity.login.network.response;

import lombok.Builder;
import lombok.Data;

/**
 * Artist 프로필 수정 응답 데이터
 */

@Data
@Builder
public class ArtistProfileApiResponse {

    private String email;
    //artist
    private String artistName;

    private String description;

    private String profileImg;

}