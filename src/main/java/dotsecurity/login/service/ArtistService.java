package dotsecurity.login.service;

import dotsecurity.login.application.exception.AuthNotAllowedException;
import dotsecurity.login.application.exception.DuplicatedException;
import dotsecurity.login.application.exception.EmailExistedException;
import dotsecurity.login.domain.Artist;
import dotsecurity.login.domain.RoleName;
import dotsecurity.login.domain.User;
import dotsecurity.login.domain.repository.ArtistRepository;
import dotsecurity.login.domain.repository.UserRepository;
import dotsecurity.login.network.request.ArtistConfirmApiRequest;
import dotsecurity.login.network.request.UserApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistService {


    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;


    public Long createArtist(ArtistConfirmApiRequest userData) {

        //유저 정보 가져오기
        User user = userRepository.findByEmail(userData.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email"));

        // 아티스트 닉네임 중복 방지
        if (artistRepository.findByArtistName(userData.getArtistName()).isPresent()) {
            throw new DuplicatedException(userData.getArtistName());
        }


        // user.id == artist.id
        Artist newArtist = Artist.builder()
                .user(user)
                .description(userData.getDescription())
                .artistName(userData.getArtistName())
                .profileImg(userData.getProfileImg())
                .build();

        artistRepository.save(newArtist);

        return newArtist.getId();

    }




}
