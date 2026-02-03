package com.svu.resume.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.svu.resume.document.User;
import com.svu.resume.dto.AuthResponse;
import com.svu.resume.dto.RegisterRequest;
import com.svu.resume.exception.ResourceExistsException;
import com.svu.resume.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    @Value("${app.base.url}")
    private String appUrl;
    private final EmailService emailService;
    public AuthResponse register(RegisterRequest request){
        log.info("inside register() method{}",request);
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceExistsException("user email already exists");
        }
        User newUser=toDocument(request);
        userRepository.save(newUser);


        sendVerificationEmail(newUser);

        return toResponse(newUser);
    }

    private void sendVerificationEmail(User newUser) {
        log.info("inside AuthService - sending verification email :{}",newUser);
       try{
            String link=appUrl+"/api/auth/verify-email?token="+newUser.getVerficationToken();
            String html = 
    "<div style='font-family:Arial,sans-serif;'>" +
        "<h2>Verify your email</h2>" +
        "<p>Hi " + newUser.getName() + ",</p>" +
        "<p>Please confirm your email to activate your account.</p>" +

        "<p>" +
            "<a href='" + link + "' " +
               "style='display:inline-block;padding:10px 16px;" +
               "background:#6366f1;color:#fff;text-decoration:none;" +
               "border-radius:6px;'>" +
               "Verify Email</a>" +
        "</p>" +

        "<p>Or copy this link:</p>" +
        "<p>" + link + "</p>" +

        "<p>This link expires in 24 hours.</p>" +
    "</div>";
        emailService.sendHTMLMail(newUser.getEmail(),"Verify your Email",html);
        log.info("in email sending");
       }
       catch(Exception e){
        log.error("error occured at sending verfication email");
        throw new RuntimeException("failed to send verfication Mail"+e.getMessage());
       }
    }
    private AuthResponse toResponse(User newUser){
        AuthResponse userResponse=AuthResponse.builder()
            .name(newUser.getName())    
            .email(newUser.getEmail())  
            .password(newUser.getPassword())
            .profileImageUrl(newUser.getProfileImageUrl())
            .emailVerified(newUser.isEmailVerified())
            .subscriptionPlan(newUser.getSubscriptionPlan())
            .createdAt(newUser.getCreatedAt())
            .updatedAt(newUser.getUpdatedAt())
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

    public String verifyEmail(String token){
        log.info("inside authservice verify email");
        User user=userRepository.findByVerficationToken(token)
        .orElseThrow(() -> new RuntimeException("Token is invalid or expired verification"));
        log.info("inside authservice after verify email");
        if(user.getVerificationExpires()!=null && user.getVerificationExpires().isBefore(LocalDateTime.now())){
            log.info("2nd ex");
            throw new RuntimeException("verification token code expired");
        }
        
        user.setEmailVerified(true);
        user.setVerficationToken(null);
        user.setVerificationExpires(null);
        userRepository.save(user);
        return "email verfied sucessfully";
    }
}
