package dotsecurity.login.network.request;

import lombok.Data;

@Data
public class ArtistConfirmApiRequest {

    private String email;

    private String artistName;

    private String genre;

    private String introduction;

    private String socialLink;
}
