package dotsecurity.login.service;

import dotsecurity.login.application.exception.ArtistNotEnrolledIdException;
import dotsecurity.login.domain.Artist;
import dotsecurity.login.domain.User;
import dotsecurity.login.domain.repository.ArtistRepository;
import dotsecurity.login.domain.repository.UserRepository;
import dotsecurity.login.network.request.UserProfileApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    public void updateUserProfileName(UserProfileApiRequest data) {

        User user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email"));

        user.setName(data.getName());

        userRepository.save(user);

    }

    public void updateUserProfileNickname(UserProfileApiRequest data) {
        User user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email"));

        user.setNickname(data.getNickname());


        userRepository.save(user);

    }

    public void updateArtistProfileName(UserProfileApiRequest data) {
        User user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email"));

        Artist artist = artistRepository.findById(user.getId())
                .orElseThrow(() -> new ArtistNotEnrolledIdException());

        artist.setArtistName(data.getArtistName());

        artistRepository.save(artist);
    }

    public void updateArtistProfileDescription(UserProfileApiRequest data) {
        User user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email"));

        Artist artist = artistRepository.findById(user.getId())
                .orElseThrow(() -> new ArtistNotEnrolledIdException());

        artist.setDescription(data.getDescription());

        artistRepository.save(artist);
    }

    public void updateArtistProfileImage(UserProfileApiRequest data) {
        User user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email"));

        Artist artist = artistRepository.findById(user.getId())
                .orElseThrow(() -> new ArtistNotEnrolledIdException());

        artist.setDescription(data.getDescription());

        artistRepository.save(artist);
    }
}
