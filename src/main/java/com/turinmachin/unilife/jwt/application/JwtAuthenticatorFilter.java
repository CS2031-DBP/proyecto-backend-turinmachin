package com.turinmachin.unilife.jwt.application;

import com.turinmachin.unilife.jwt.domain.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticatorFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {
        final String jwtToken = resolveToken(request);

        if (StringUtils.hasText(jwtToken)) {
            final Optional<Claims> maybeClaims = jwtService.extractAllClaims(jwtToken);
            final Optional<Authentication> maybeAuth = maybeClaims
                    .flatMap(claims -> jwtService.getAuthentication(jwtToken, claims));

            maybeAuth.ifPresent(SecurityContextHolder.getContext()::setAuthentication);
        }

        filterChain.doFilter(request, response);

        // Reset authentication after request
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private String resolveToken(final HttpServletRequest request) {

        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

}
