package com.example.cloudvault.demo.controllers;

import com.example.cloudvault.demo.entities.SavedFiles;
import com.example.cloudvault.demo.services.SavedFilesService;
import com.example.cloudvault.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class SavedFilesController {

    @Autowired
    private SavedFilesService savedFilesService;

    @Autowired
    private UserService userService;

    // Get all files for logged-in user
    @GetMapping("/my")
    public ResponseEntity<?> getMyFiles(@AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.findByEmail(userDetails.getUsername()).getUserId();

        return ResponseEntity.ok(
                Map.of("status", true,
                        "files", savedFilesService.getFilesByUser(userId))
        );
    }

    // Delete file by id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        savedFilesService.deleteFile(id);
        return ResponseEntity.ok(Map.of("status", true, "message", "Deleted"));
    }
}
