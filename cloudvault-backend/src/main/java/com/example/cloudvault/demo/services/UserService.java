package com.example.cloudvault.demo.services;



import com.example.cloudvault.demo.entities.UserCredentials;
import com.example.cloudvault.demo.entities.UserEntity;
import com.example.cloudvault.demo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;


    public void register(UserEntity userEntity) {
        LocalDateTime now = LocalDateTime.now();
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setCreated_at(now);
        userEntity.setUpdated_at(now);
        userRepository.save(userEntity);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public UserEntity login(UserCredentials userCredentials) {

        userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        return userRepository.findByEmailAndPassword(userCredentials.getEmail(), userCredentials.getPassword());
    }

    @Cacheable(key = "#id.toString()")
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    //@Cacheable(key = "#email")
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }
}

