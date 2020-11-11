package dotsecurity.login.security;

import dotsecurity.login.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    private Key key;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.ExpirationInMs}")
    private int jwtExpInMs;


    public String generateToken(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        this.key = Keys.hmacShaKeyFor(secret.getBytes());

        Date time = new Date();
        Date expiryDate = new Date(time.getTime() + jwtExpInMs);

        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(userPrincipal))
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromJwt(String token){

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        log.info("userid from jwt : " + claims.getId() + "," + claims.getSubject());

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken){
        try{

            this.key = Keys.hmacShaKeyFor(secret.getBytes());

            log.info("validate token : " + authToken);
            Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(authToken)
                    .getBody();
            return true;
        }
        catch (SignatureException ex){
            log.error("InValid JWT Signature");
        }catch (MalformedJwtException ex){
            log.error("InValid JWT token");
        }
        return false;

    }




//   token set
    private static Map<String, Object> createHeader(){
        Map<String, Object> header = new HashMap<>();

        header.put("regDate", System.currentTimeMillis());
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        return header;
    }

    private Map<String, Object> createClaims(UserPrincipal user){
        Map<String, Object> claims = new HashMap<>();

        claims.put("role", user.getAuthorities());
        claims.put("email", user.getEmail());
        claims.put("id", user.getId());

        return claims;

    }

}
