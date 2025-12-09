package com.example.cloudvault.demo.services;

import com.example.cloudvault.demo.entities.SavedFiles;
import com.example.cloudvault.demo.entities.UserEntity;
import com.example.cloudvault.demo.repositories.SavedFilesRepository;
import com.example.cloudvault.demo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SavedFilesService {

    @Autowired
    private SavedFilesRepository savedFilesRepository;

    @Autowired
    private UserRepository userRepository;

    public SavedFiles saveFile(Long userId, String fileUrl) {
        UserEntity user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        log.info("Saving file " + fileUrl + " to user " + user.getUsername());
        SavedFiles savedFile = SavedFiles.builder()
                .fileLink(fileUrl)
                .user(user)
                .build();
        log.info("Saving file " + savedFile.getFileLink() + " to user " + user.getUsername());
        return savedFilesRepository.save(savedFile);
    }

    public List<SavedFiles> getFilesByUser(Long userId) {
        return savedFilesRepository.findByUserUserId(userId);
    }


    public void deleteFile(Long id) {
        savedFilesRepository.deleteById(id);
    }

    public List<SavedFiles> getAllSavedFilesForUser(Long userId) {
        return savedFilesRepository.findByUserUserId(userId);
    }
}
