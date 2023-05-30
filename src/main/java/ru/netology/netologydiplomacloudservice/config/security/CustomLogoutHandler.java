package ru.netology.netologydiplomacloudservice.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    @Value("${auth.header}")
    private String authHeader;
    private final TokenInMemoryStorage tokenStorage;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String token = request.getHeader(authHeader).substring(7);

        if (tokenStorage.deleteToken(token)) {
            log.info("Token {} was deleted from storage", token);
        } else {
            log.warn("Token {} was not deleted. There is no such record in token storage", token);
        }
    }
}
