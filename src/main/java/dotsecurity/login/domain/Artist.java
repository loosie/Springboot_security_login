package dotsecurity.login.domain;


import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Artist {

    @Id
    @Column(name = "artist_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "artist_id", referencedColumnName="user_id")
    private User user;

    private String artistName;

    private String description;

    private String profileImg;


}
