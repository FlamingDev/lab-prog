package infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;

import java.time.Instant;

@Service
public class JwtService {
    @Value("${api.security.jwt.secret}")
    private String secret;

    @Value("${api.security.jwt.expiration-time}")
    private Long expirationTimeSeconds;

    public String generateToken(Authentication authentication) {

        Instant now = Instant.now();

        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return   JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(authentication.getName())
                    .withExpiresAt(now.plusSeconds(expirationTimeSeconds))
                    .sign(algorithm);
        }
        catch(JWTCreationException exception){
            throw new RuntimeException("Error while creating JWT: " + exception.getMessage());
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        }
        catch (JWTVerificationException e){
            return null;
        }
    }
}
