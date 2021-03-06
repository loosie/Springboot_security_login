package dotsecurity.login.domain.repository;

import dotsecurity.login.domain.Role;
import dotsecurity.login.domain.RoleName;
import dotsecurity.login.domain.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);

    Optional<Role> findById(Long id);

}
