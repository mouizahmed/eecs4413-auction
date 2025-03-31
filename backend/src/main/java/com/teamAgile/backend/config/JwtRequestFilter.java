package com.teamAgile.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.UserService;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String JWT_COOKIE_NAME = "jwt";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            // Try to get token from cookie first
            String token = extractJwtFromCookies(request);

            // If not found in cookie, try Authorization header
            if (token == null) {
                token = extractJwtFromHeader(request);
            }

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtUtils.extractUsername(token);

                if (username != null) {
                    User user = userService.findByUsername(username);

                    if (user != null && isTokenValid(token, user)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        chain.doFilter(request, response);
    }

    private boolean isTokenValid(String token, User user) {
        final String username = jwtUtils.extractUsername(token);
        return (username.equals(user.getUsername()) && !jwtUtils.extractExpiration(token).before(new java.util.Date()));
    }

    private String extractJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private String extractJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}