package io.github.rohergun.recipe_hub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private static final long EXPIRATION = 86400000L;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserDetails user) {

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis() + EXPIRATION
                        )
                ).signWith(key).compact();
    }

    public String extractUsername(String token) {

        return extractClaims(token)
                .getSubject();
    }


    public boolean isTokenValid(
            String token,
            UserDetails user
    ) {

        String username = extractUsername(token);

        return username.equals(user.getUsername())
                && !extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractClaims(String token){

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
