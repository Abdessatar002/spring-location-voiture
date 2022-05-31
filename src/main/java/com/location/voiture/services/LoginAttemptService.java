package com.location.voiture.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPT = 5;
    private static final int ATTEMPT_INCREMENT = 1;
    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username) {
        int attempt = 0;
        try {
            attempt = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
            loginAttemptCache.put(username, attempt);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean hasExceededMaxOfAttempt(String username) {
        try {
            return loginAttemptCache.get(username) > MAXIMUM_NUMBER_OF_ATTEMPT;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
