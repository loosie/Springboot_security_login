package dotsecurity.login.network.response;

import dotsecurity.login.domain.Role;
import dotsecurity.login.domain.UserHasRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import java.util.List;
import java.util.Set;

/**
 * User 응답 Data
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserApiResponse {

    private Long id;

    private String email;

    private String name;

    private String password;

    private List<UserHasRole> roleList;

}