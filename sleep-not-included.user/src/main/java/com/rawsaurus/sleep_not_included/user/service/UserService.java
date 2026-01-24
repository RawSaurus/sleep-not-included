package com.rawsaurus.sleep_not_included.user.service;

import com.rawsaurus.sleep_not_included.user.dto.UserRequest;
import com.rawsaurus.sleep_not_included.user.dto.UserResponse;
import com.rawsaurus.sleep_not_included.user.mapper.UserMapper;
import com.rawsaurus.sleep_not_included.user.model.User;
import com.rawsaurus.sleep_not_included.user.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;

    public UserResponse findUser(Long userId){
        return userMapper.toResponse(
                userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
        );
    }

    public UserResponse findUserByName(String username){
        return userMapper.toResponse(
                userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
        );
    }

    public Page<UserResponse> findUsersByNameLike(String username, Pageable pageable){
        return userRepo.findAllByUsernameLikeIgnoreCase(username, pageable)
                .map(userMapper::toResponse);
    }

    public UserResponse createUser(UserRequest request){
        return userMapper.toResponse(
                userRepo.save(
                        userMapper.toEntity(request)
                )
        );
    }

    public UserResponse updateUser(Long userId){
        return null;
    }

    public String deleteUser(Long userId){
        return null;
    }
}
