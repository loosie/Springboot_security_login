package dotsecurity.login.security;

import dotsecurity.login.domain.User;
import io.jsonwebtoken.*;
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
//@Component
public class JwtTokenProvider {

    private Key key;
    private Date expiryDate;

    public JwtTokenProvider(String secret, int jwtExpInMs){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        Date time = new Date();
        expiryDate = new Date(time.getTime() + jwtExpInMs);

    }


    public String generateToken(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

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

        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        log.info("userId from jwt : " + claims.getSubject());

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken){
        try{

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
        }catch (ExpiredJwtException ex){
            log.error("Expired JWT token");
        }catch (UnsupportedJwtException ex){
            log.error("Unsupported JWT token");
        }catch (IllegalArgumentException ex){
            log.error("JWT claims string is empty");
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
