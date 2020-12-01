package dotsecurity.login.controller;

import dotsecurity.login.network.request.ArtistConfirmApiRequest;
import dotsecurity.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {


    @Autowired
    private UserService userService;


    /**
     * 유저 패스워드 초기화
     * 일단 , email 입력 -> 입력된 이메일로 초기화된 비밀번호 보내기
     */


    /**
     * 아티스트 등록
     */
    @PostMapping("/artist-enroll")
    public String giveRoleArtist(@RequestBody ArtistConfirmApiRequest request){

        if(!userService.checkIsArtistProfile(request)){
            return "failure to get role_artist";
        }

        return "success to get role_artist";
    }


}
