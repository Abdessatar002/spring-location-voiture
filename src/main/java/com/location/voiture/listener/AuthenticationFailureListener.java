package com.location.voiture.listener;


import com.location.voiture.services.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener {
    @Autowired
    private LoginAttemptService loginAttemptService;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String){
            loginAttemptService.addUserToLoginAttemptCache((String) principal);
        }
    }
}
