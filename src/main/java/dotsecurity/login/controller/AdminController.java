package dotsecurity.login.controller;

import dotsecurity.login.network.Header;
import dotsecurity.login.network.request.ArtistConfirmApiRequest;
import dotsecurity.login.network.response.ConfirmApiResponse;
import dotsecurity.login.service.ArtistService;
import dotsecurity.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArtistService artistService;


    /**
     * 유저 패스워드 초기화
     * 일단 , email 입력 -> 입력된 이메일로 초기화된 비밀번호 보내기
     */


    /**
     * 아티스트 등록
     */
    @PostMapping("/artist-enroll")
    public Header<ConfirmApiResponse> giveRoleArtist(@RequestBody Header<ArtistConfirmApiRequest> request){

        ArtistConfirmApiRequest data = request.getData();

        // 이메일 인증 여부 확인 후 isArtist = true
        if(!userService.checkIsArtistProfile(data)){

            return Header.ERROR("failure to get role_artist");
        }

        Long id = artistService.createArtist(data);

        return Header.OK(
                    ConfirmApiResponse.builder()
                            .message("success to get role_artist; artist_id :" +id)
                            .build()
                        );
    }


}
