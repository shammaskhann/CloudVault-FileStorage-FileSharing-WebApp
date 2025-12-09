package com.example.cloudvault.demo.repositories;

import com.example.cloudvault.demo.entities.SavedFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedFilesRepository extends JpaRepository<SavedFiles, Long> {

    List<SavedFiles> findByUserUserId(Long userId);
}
