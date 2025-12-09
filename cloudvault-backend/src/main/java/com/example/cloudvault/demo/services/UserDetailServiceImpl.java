package com.example.cloudvault.demo.services;


import com.example.cloudvault.demo.entities.UserEntity;
import com.example.cloudvault.demo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmail(username);
        log.info(user.get().getUsername());
        if(user.isPresent()){
            UserEntity userEntity = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userEntity.getEmail())  // Use email as the username
                    .password(userEntity.getPassword())  // Password remains the same
                    .roles("CUSTOMER")
                    .build();

        }
        else{
            throw new UsernameNotFoundException("User not found");
        }
    }
}

