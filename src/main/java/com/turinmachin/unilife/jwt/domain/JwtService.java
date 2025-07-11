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

    public String generateToken(final User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("https://unilife.lat/role", user.getRole().name())
                .claim("role", "authenticated")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigningKey())
                .compact();
    }

    public Optional<Claims> extractAllClaims(final String jwtToken) {
        try {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
            return Optional.of(claims);
        } catch (final JwtException e) {
            return Optional.empty();
        }
    }

    private <T> T extractClaim(final Claims claims, final Function<Claims, T> resolver) {
        return resolver.apply(claims);
    }

    public Optional<Authentication> getAuthentication(final String jwtToken, final Claims claims) {
        final UUID id = UUID.fromString(extractClaim(claims, Claims::getSubject));
        final Optional<User> maybeUser = userService.getUserById(id);
        return maybeUser.map(user -> new UsernamePasswordAuthenticationToken(user, jwtToken, user.getAuthorities()));
    }

    private Key getSigningKey() {
        final byte[] bytes = secret.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }

}
