package com.rawsaurus.sleep_not_included.user.service;

import com.rawsaurus.sleep_not_included.user.dto.DeleteEntityEvent;
import com.rawsaurus.sleep_not_included.user.dto.UserRequest;
import com.rawsaurus.sleep_not_included.user.dto.UserResponse;
import com.rawsaurus.sleep_not_included.user.mapper.UserMapper;
import com.rawsaurus.sleep_not_included.user.model.User;
import com.rawsaurus.sleep_not_included.user.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private String queueName = "image.entity.deleted.queue";
    private String exchangeName = "user.events";
    private String routingKey = "entity.deleted";

    private final UserRepository userRepo;

    private final UserMapper userMapper;

    private final RabbitTemplate rabbitTemplate;

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
        return userRepo.findAllByusernameLikeIgnoreCase(username, pageable)
                .map(userMapper::toResponse);
    }

    public UserResponse createUser(UserRequest request){
        return userMapper.toResponse(
                userRepo.save(
                        userMapper.toEntity(request)
                )
        );
    }

    public UserResponse updateUser(Long userId, UserRequest request){
        //check for user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userMapper.updateToEntity(request, user);

        return userMapper.toResponse(userRepo.save(user));
    }

    @Transactional
    public String deleteUser(Long userId){
        //check for user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        //delete related entities

        userRepo.delete(user);
        rabbitTemplate.convertAndSend(exchangeName, "",
                new DeleteEntityEvent("USER", userId));

        return "User deleted successfully";
    }
}
