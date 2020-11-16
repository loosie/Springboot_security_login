package dotsecurity.login.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "role")
public class Role {

    @Id @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    private RoleName name;

//    @OneToMany(mappedBy = "role")
//    private List<UserHasRole> lists;


//    @ManyToOne
//    @JoinColumn(name = "parent_user_id")
//    private Role parentRole;

}
