package com.rawsaurus.sleep_not_included.user.service;

import com.rawsaurus.sleep_not_included.user.dto.UserRequest;
import com.rawsaurus.sleep_not_included.user.dto.UserResponse;
import com.rawsaurus.sleep_not_included.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepo;

    public UserResponse findUser(Long userId){
        return null;
    }

    public UserResponse findUserByName(String username){
        return null;
    }

    public UserResponse createUser(UserRequest request){
        return null;
    }

    public UserResponse updateUser(Long userId){
        return null;
    }

    public String deleteUser(Long userId){
        return null;
    }
}
