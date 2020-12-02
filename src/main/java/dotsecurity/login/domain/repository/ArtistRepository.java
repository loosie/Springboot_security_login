package dotsecurity.login.domain.repository;

import dotsecurity.login.domain.Artist;
import dotsecurity.login.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByArtistName(String artistName);
}
