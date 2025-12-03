package com.example.notificationsse.application.usecase.getallusers;

import com.example.notificationsse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllUsersService implements GetAllUsers {
    
    private final UserRepository userRepository;
    
    @Override
    public List<UserRes> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserRes::from)
            .toList();
    }
}


