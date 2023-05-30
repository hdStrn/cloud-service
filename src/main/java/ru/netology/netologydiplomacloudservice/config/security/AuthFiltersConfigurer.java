package ru.netology.netologydiplomacloudservice.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/*
Конфигурирование нашего кастомного фильтра, присвоение ему созданного спрингом менеджера и наших обработчиков,
и добавление этого фильтра в цепочку вместо стандартного фильтра (UsernamePasswordAuthenticationFilter)
 */
@Component
@RequiredArgsConstructor
public class AuthFiltersConfigurer extends AbstractHttpConfigurer<AuthFiltersConfigurer, HttpSecurity> {

    private final TokenAuthFilter tokenAuthFilter;
    private final CustomAuthSuccessHandler successHandler;
    private final CustomAuthFailureHandler failureHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

        JsonAuthFilter filter = new JsonAuthFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);

        http.addFilterAt(filter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
