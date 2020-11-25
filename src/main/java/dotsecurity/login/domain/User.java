package dotsecurity.login.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User  {

    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private boolean isArtist;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "user_role",
//                joinColumns = @JoinColumn(name = "user_id"),
//                inverseJoinColumns = @JoinColumn(name = "role_id"))
//    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<UserHasRole> userRoles;

    //==생성 메서드==//


    //==연관관계 메서드==//
    public void setUserRoles(UserHasRole userRole){
        userRoles.add(userRole);
        userRole.setUser(this);
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }


    public void completeEmailConfirm() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();

    }

    public void artistEnrollment() {
        this.isArtist = true;
    }
}

