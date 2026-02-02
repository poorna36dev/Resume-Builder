package com.svu.resume.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svu.resume.document.User;
import com.svu.resume.dto.AuthResponse;
import com.svu.resume.dto.RegisterRequest;
import com.svu.resume.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request){
        log.info("inside register() method{}",request);
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("user email already exists");
        }
        User newUser=toDocument(request);
        userRepository.save(newUser);

        //TODO send email verification


        return toResponse(newUser);
    }
    private AuthResponse toResponse(User newUser){
        AuthResponse userResponse=AuthResponse.builder()
            .name(newUser.getName())    
            .email(newUser.getEmail())  
            .password(newUser.getPassword())
            .profileImageUrl(newUser.getProfileImageUrl())
            .emailVerified(newUser.isEmailVerified())
            .subscriptionPlan(newUser.getSubscriptionPlan())
            .build();
            return userResponse;
    }
    private User toDocument(RegisterRequest request){
        return User.builder()
            .name(request.getName())    
            .email(request.getEmail())  
            .password(request.getPassword())
            .profileImageUrl(request.getProfileImageUrl())
            .subscriptionPlan("basic")
            .emailVerified(false)
            .verficationToken(UUID.randomUUID().toString())
            .verificationExpires(LocalDateTime.now().plusHours(24))
            .build();
    }
}
