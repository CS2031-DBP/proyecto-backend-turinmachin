package com.turinmachin.unilife.jwt.domain;

import com.turinmachin.unilife.common.exception.UnauthorizedException;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private UserService userService;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public UUID extractId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
    }

    public void validateToken(String jwt, UUID id) {
        User user = userService.getUserById(id).orElseThrow(UnauthorizedException::new);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        var authToken = new UsernamePasswordAuthenticationToken(user, jwt, user.getAuthorities());

        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }

    private Key getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }
}
