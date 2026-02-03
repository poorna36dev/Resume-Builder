package com.svu.resume.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.svu.resume.dto.AuthResponse;
import com.svu.resume.dto.RegisterRequest;
import com.svu.resume.service.AuthService;
import static com.svu.resume.util.AppConstants.AUTH_CONTROLLER;
import static com.svu.resume.util.AppConstants.REGISTER;
import static com.svu.resume.util.AppConstants.VERIFY_EMAIL;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {
    private final AuthService authService;

    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){ 
        log.info("in authController-register()");     
        AuthResponse response=authService.register(request);
        log.info("response from service:{}",response);  
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        log.info("in authController-verifyEmail()"); 
        if(token == null){
            return ResponseEntity.badRequest()
            .body(Map.of("message", "Token is missing"));
        }
        String res=authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message",res));  
    }
}
