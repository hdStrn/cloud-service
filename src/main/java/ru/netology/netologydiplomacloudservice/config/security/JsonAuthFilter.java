package ru.netology.netologydiplomacloudservice.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.netology.netologydiplomacloudservice.exception.IncorrectInputDataException;

import java.io.IOException;
import java.util.Map;

/*
Кастомный секьюрити фильтр, чтобы вытаскивать юзернейм и пароль из json, а не из параметров запроса
 */
@Slf4j
public class JsonAuthFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {

        String username;
        String password;

        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);

            username = requestMap.get("login");
            if (username == null) {
                log.warn("There is no 'username' field in the request");
                throw new IncorrectInputDataException("Username is mandatory");
            }

            password = requestMap.get("password");
            if (password == null) {
                log.warn("There is no 'password' field in the request");
                throw new IncorrectInputDataException("Password is mandatory");
            }
        } catch (IOException | RuntimeException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            username, password);
        this.setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
