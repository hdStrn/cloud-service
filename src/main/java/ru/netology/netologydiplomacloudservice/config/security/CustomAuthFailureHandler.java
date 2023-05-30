package ru.netology.netologydiplomacloudservice.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import ru.netology.netologydiplomacloudservice.api.dto.ErrorDto;

import java.io.IOException;

/*
Обработчик для неуспешной авторизации - возвращаем ошибку
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        ErrorDto error = new ErrorDto(exception.getMessage());
        response.getWriter().append(mapper.writeValueAsString(error));
        response.setContentType("application/json");
        response.setStatus(400);
    }
}
