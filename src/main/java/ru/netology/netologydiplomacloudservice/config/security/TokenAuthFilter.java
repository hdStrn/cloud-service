package ru.netology.netologydiplomacloudservice.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
Кастомный секьюрити фильтр аутентификации по токену
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthFilter extends OncePerRequestFilter {

    @Value("${auth.header}")
    private String authHeader;
    private final TokenInMemoryStorage tokenStorage;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        final String tokenRaw = request.getHeader(authHeader);
        if (tokenRaw == null || !tokenRaw.startsWith("Bearer")) {
            log.warn("There is no correct token header in the request");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = tokenRaw.substring(7);
        final UserDetails user = tokenStorage.getUserByToken(token);
        if (user == null) {
            log.warn("There is no user matching for token {}", token);
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User {} successfully authenticated by token", user.getUsername());

        filterChain.doFilter(request, response);
    }
}
