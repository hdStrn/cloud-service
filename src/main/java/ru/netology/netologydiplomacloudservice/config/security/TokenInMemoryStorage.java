package ru.netology.netologydiplomacloudservice.config.security;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class TokenInMemoryStorage {

    private Map<String, UserDetails> tokenStorage = new ConcurrentHashMap<>();

    public void addToken(String token, UserDetails user) {
        tokenStorage.put(token, user);
    }

    public UserDetails getUserByToken(String token) {
        return tokenStorage.get(token);
    }

    public boolean deleteToken(String token) {
        return tokenStorage.remove(token) != null;
    }
}
