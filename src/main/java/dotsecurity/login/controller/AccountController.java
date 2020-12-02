package dotsecurity.login.controller;

import dotsecurity.login.network.Header;
import dotsecurity.login.network.request.UserProfileApiRequest;
import dotsecurity.login.network.response.ArtistProfileApiResponse;
import dotsecurity.login.network.response.UserProfileApiResponse;
import dotsecurity.login.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AccountController {


    private final AccountService accountService;

    //TODO : 임의로 작성. 디벨롭 필요
    @PatchMapping("/user/{userId}")
    public Header<UserProfileApiResponse> changeUserProfile(@RequestBody Header<UserProfileApiRequest> request){

        UserProfileApiRequest data = request.getData();

        //경 닉네임만 변경
        if(data.getName().equals("") && !data.getNickname().isEmpty() ) {
            accountService.updateUserProfileNickname(data);

        }

        // 이름만 변
        else if(data.getNickname().equals("") && !data.getName().isEmpty()) {
            accountService.updateUserProfileName(data);

        }

        // 둘다 변경
        else {
            accountService.updateUserProfileNickname(data);
            accountService.updateUserProfileName(data);
        }

        return Header.OK(
                UserProfileApiResponse.builder()
                        .email(data.getEmail())
                        .name(data.getName())
                        .nickname(data.getNickname())
                        .build()
        );
    }



    //TODO :  좀 더 독립적으로 변경하기 (변경된 부분만 탐지해서 입력값 넣어주기)
    @PatchMapping("/artist/{artistId}")
    public Header<ArtistProfileApiResponse> changeArtistProfile(@RequestBody Header<UserProfileApiRequest> request){

        UserProfileApiRequest data = request.getData();

        // 아티스트명만 변경
        if(data.getProfileImg().equals("") && data.getDescription().equals("")) {
            accountService.updateArtistProfileName(data);

        }

        // 소개만 변경
        else if(data.getArtistName().equals("") && data.getProfileImg().equals("") ) {
            accountService.updateArtistProfileDescription(data);

        }

        // 프로필 이미지만 변경
        else if(data.getArtistName().equals("") && data.getDescription().equals("")){
            accountService.updateArtistProfileImage(data);

        }

        // 모두 변경
        else {
            accountService.updateArtistProfileName(data);
            accountService.updateArtistProfileDescription(data);
            accountService.updateArtistProfileImage(data);
        }

        return Header.OK(
                ArtistProfileApiResponse.builder()
                        .email(data.getEmail())
                        .artistName(data.getArtistName())
                        .description(data.getDescription())
                        .profileImg(data.getProfileImg())
                        .build()
        );
    }
}
