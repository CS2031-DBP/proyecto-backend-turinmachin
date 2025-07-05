package com.turinmachin.unilife.jwt.domain;

import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private final UserService userService;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("https://unilife.lat/role", user.getRole().name())
                .claim("role", 16479)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigningKey())
                .compact();
    }

    public Optional<Claims> extractAllClaims(String jwtToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
            return Optional.of(claims);
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    private <T> T extractClaim(Claims claims, Function<Claims, T> resolver) {
        return resolver.apply(claims);
    }

    public Optional<Authentication> getAuthentication(String jwtToken, Claims claims) {
        UUID id = UUID.fromString(extractClaim(claims, Claims::getSubject));
        Optional<User> maybeUser = userService.getUserById(id);
        return maybeUser.map(user -> new UsernamePasswordAuthenticationToken(user, jwtToken, user.getAuthorities()));
    }

    private Key getSigningKey() {
        byte[] bytes = secret.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }

}
