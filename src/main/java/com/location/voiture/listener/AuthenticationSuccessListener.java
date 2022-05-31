package com.location.voiture.listener;


import com.location.voiture.models.UserPrincipal;
import com.location.voiture.services.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {
    @Autowired
    private LoginAttemptService loginAttemptService;

    @EventListener
    private void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal){
            UserPrincipal userPrincipal = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(userPrincipal.getUsername());
        }
    }
}
