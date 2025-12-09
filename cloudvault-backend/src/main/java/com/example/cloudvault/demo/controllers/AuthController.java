package com.example.cloudvault.demo.controllers;


import com.example.cloudvault.demo.entities.RequestBodyEntities.RegisterRequest;
import com.example.cloudvault.demo.entities.UserCredentials;
import com.example.cloudvault.demo.entities.UserEntity;
import com.example.cloudvault.demo.services.UserService;
import com.example.cloudvault.demo.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private UserService userService;// User Service to get info and details
    @Autowired
    private AuthenticationManager authenticationManager;  // For Authentication the Jwt auth Tokens
    @Autowired
    private JwtUtil jwtUtil; // JWT Auth token Main Service
     @Autowired
     private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> loginPost(@RequestBody UserCredentials userCredentials) {
        if (userCredentials.getEmail() == null || userCredentials.getPassword() == null) {
            return new ResponseEntity<>(Map.of("status", false, "message", "Email or password is required"), HttpStatus.BAD_REQUEST);
        }
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCredentials.getEmail(), userCredentials.getPassword()));
            final UserEntity userEntity = userService.findByEmail(userCredentials.getEmail());
            if(userEntity == null) {
                return new ResponseEntity<>(Map.of("status", false, "message", "Wrong Email or Password "), HttpStatus.NOT_FOUND);
            }else{
                String jwtToken = jwtUtil.generateToken(userEntity.getEmail());
                return new ResponseEntity<>(Map.of("status", true, "data",Map.of(
                        "token", jwtToken,
                        "status", "true",
                        "data", userEntity
                )), HttpStatus.OK);

            }
        }catch (AuthenticationException e){
            log.error("Exception occurred while createAuthenticationToken", e);
            return new ResponseEntity<>(Map.of(
                    "status", false,
                    "message", "Invalid email or password"
            ), HttpStatus.UNAUTHORIZED);
        }
    }

//@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@PostMapping(value = "/register")
public ResponseEntity<?> registerPost(
        @RequestBody RegisterRequest request
        // @RequestPart(value = "profilePic", required = false) MultipartFile profilePic
) {
    List<String> errors = new ArrayList<>();
    if (request.getEmail() == null || request.getEmail().isEmpty()) errors.add("email is required");
    if (request.getPassword() == null || request.getPassword().isEmpty()) errors.add("password is required");
    if (request.getUsername() == null || request.getUsername().isEmpty()) errors.add("username is required");

    if (!errors.isEmpty())
        return ResponseEntity.badRequest().body(Map.of("status", false, "message", errors));

    try {
        if (userService.checkEmailExists(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Email already exists"));
        }

        UserEntity user = getUserEntity(request);
        userService.register(user);
        return ResponseEntity.ok(Map.of("status", true, "message", "User created successfully"));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", false, "message", "Error occurred while creating user"));
        }
    }



    private static UserEntity getUserEntity(RegisterRequest request) {
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return user;
    }
}

