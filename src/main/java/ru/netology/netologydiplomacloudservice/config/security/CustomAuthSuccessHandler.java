package ru.netology.netologydiplomacloudservice.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.netology.netologydiplomacloudservice.api.dto.LoginDto;

import java.io.IOException;
import java.util.UUID;

/*
Обработчик при успешной авторизации, чтобы вернуть токен
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper mapper;
    private final TokenInMemoryStorage tokenStorage;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        final String token = UUID.randomUUID().toString();
        response.getWriter().append(mapper.writeValueAsString(new LoginDto(token)));
        response.setStatus(200);

        UserDetails user = (UserDetails) authentication.getPrincipal();
        tokenStorage.addToken(token, user);

        log.info("User {} successfully authenticated by username and password", user.getUsername());
    }
}
