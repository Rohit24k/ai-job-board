package com.rohit.ai_job_board.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
//    @PostConstruct
//    public void init() {
//        System.out.println("JWT Secret = " + secret);
//    }
    private static final String SECRET =
            "c29tZV9yZWFsbHlfbG9uZ19iYXNlNjRfc2VjcmV0X2tleV9mb3Jfand0X2FwcGxpY2F0aW9u";

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET)
        );
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(
            String token,
            Function<Claims,T> resolver){

        Claims claims = Jwts.parser()
                .verifyWith((SecretKey)getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

    public String generateToken(UserDetails user){

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis()+expiration)
                )
                .signWith((SecretKey)getSignKey())
                .compact();
    }

    public boolean isTokenValid(
            String token,
            UserDetails user){

        return extractUsername(token)
                .equals(user.getUsername())
                &&
                extractClaim(
                        token,
                        Claims::getExpiration
                ).after(new Date());

    }

}