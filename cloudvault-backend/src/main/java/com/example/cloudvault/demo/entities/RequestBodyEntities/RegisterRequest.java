package com.example.cloudvault.demo.entities.RequestBodyEntities;

import lombok.Getter;
import lombok.Setter;

// DTO
@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    // getters & setters
}
