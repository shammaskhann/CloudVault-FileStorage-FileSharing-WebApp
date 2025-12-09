package com.example.cloudvault.demo.controllers;


import com.example.cloudvault.demo.entities.UserEntity;
import com.example.cloudvault.demo.services.SavedFilesService;
import com.example.cloudvault.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SavedFilesService savedFilesService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/get-all-users")
    ResponseEntity<?> getAllUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String search) {

        try {
            UserEntity user = userService.findByEmail(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("status", false, "message", "User not found"));
            }

            // Get all users except current user
            List<UserEntity> allUsersFetched = userService.getAllUsers()
                    .stream()
                    .filter(u -> !u.getUserId().equals(user.getUserId()))
                    .collect(Collectors.toList());



            return ResponseEntity.ok(Map.of("status", true, "users", allUsersFetched));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", false, "message", e.getMessage()));
        }
    }

    //Get All User Saved Files
    @GetMapping("/get-saved-files")
    ResponseEntity<?> getAllSavedFiles(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try{
            UserEntity user = userService.findByEmail(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("status", false, "message", "User not found"));
            }

            // Get all saved files for the user
            var savedFiles = savedFilesService.getAllSavedFilesForUser(user.getUserId());

            return ResponseEntity.ok(Map.of("status", true, "savedFiles", savedFiles));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(Map.of("status", false, "message", e.getMessage()));
        }
    }

}
