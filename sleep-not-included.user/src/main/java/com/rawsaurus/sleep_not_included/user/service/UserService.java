package com.rawsaurus.sleep_not_included.user.service;

import com.rawsaurus.sleep_not_included.user.config.RabbitMQConfig;
import com.rawsaurus.sleep_not_included.user.dto.DeleteEntityEvent;
import com.rawsaurus.sleep_not_included.user.dto.UpdateImageUrlEvent;
import com.rawsaurus.sleep_not_included.user.dto.UserRequest;
import com.rawsaurus.sleep_not_included.user.dto.UserResponse;
import com.rawsaurus.sleep_not_included.user.mapper.UserMapper;
import com.rawsaurus.sleep_not_included.user.model.User;
import com.rawsaurus.sleep_not_included.user.model.UserRole;
import com.rawsaurus.sleep_not_included.user.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepo;

    private final KeycloakAdminService keycloakAdminService;

    private final UserMapper userMapper;

    private final RabbitTemplate rabbitTemplate;

    public String test(String id){
        return keycloakAdminService.getUserById(id).toString();
    }

    public UserResponse findUser(Long userId){
        return userMapper.toResponse(
                userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
        );
    }

    public List<UserResponse> findAllByIds(List<Long> ids){
        return userRepo.findAllByIdIn(ids)
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse findUserByName(String username){
        return userMapper.toResponse(
                userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
        );
    }

    public UserResponse findUserByKeycloakId(String keycloakId){
        return userMapper.toResponse(
                userRepo.findByKeycloakId(keycloakId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"))
        );
    }

    public List<UserResponse> findUsersByNameLike(String username){
        Pageable pageable = PageRequest.of(0,5);
        return userRepo.searchUsers(username, pageable)
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse createUser(UserRequest request){
        String token = keycloakAdminService.getAdminAccessToken();
        String keycloakId = keycloakAdminService.createUser(token, request);

        User user = userMapper.toEntity(request);
        user.setKeycloakId(keycloakId);
        user.setRole(UserRole.USER);

        keycloakAdminService.assignRealRoleToUser(
                token,
                request.username(),
                "USER",
                keycloakId
                );

        return userMapper.toResponse(
                userRepo.save(user)
        );
    }

    public String checkKeycloakAndCreateUser(String keycloakId){
        if(userRepo.findByKeycloakId(keycloakId).isPresent()){
            return "User already exists";
        }

        var keycloakUser = keycloakAdminService.getUserById(keycloakId);
        if(keycloakUser == null){
            throw new EntityNotFoundException("Keycloak user not found");
        }

        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setUsername((String) keycloakUser.get("username"));
        user.setEmail((String) keycloakUser.get("email"));
        user.setRole(UserRole.USER);

        String token = keycloakAdminService.getAdminAccessToken();
        keycloakAdminService.assignRealRoleToUser(
                token,
                user.getUsername(),
                "USER",
                keycloakId
        );

        userRepo.save(user);
        return "User created successfully";
    }

    //TODO remove parameter, check by security token
    public UserResponse updateUser(Long userId, UserRequest request){
        //check for user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userMapper.updateToEntity(request, user);

        return userMapper.toResponse(userRepo.save(user));
    }

    //TODO remove parameter, check by security token
    @Transactional
    public String deleteUser(Long userId){
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepo.delete(user);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EVENTS_EXCHANGE,
                "",
                new DeleteEntityEvent("user", userId));

        return "User deleted successfully";
    }

    @RabbitListener(queues = RabbitMQConfig.USER_IMAGE_UPDATE_QUEUE)
    public void addProfilePicUrlToUser(UpdateImageUrlEvent event){
        System.out.println("Id: " + event.userId() + "Url: " + event.imageUrl());
        User user = userRepo.findById(event.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setProfilePicUrl(event.imageUrl());

        userRepo.save(user);
    }
}
