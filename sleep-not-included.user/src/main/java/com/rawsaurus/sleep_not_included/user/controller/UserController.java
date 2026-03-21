package com.rawsaurus.sleep_not_included.user.controller;

import com.rawsaurus.sleep_not_included.user.dto.UserRequest;
import com.rawsaurus.sleep_not_included.user.dto.UserResponse;
import com.rawsaurus.sleep_not_included.user.model.User;
import com.rawsaurus.sleep_not_included.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Log
public class UserController {

    private final UserService userService;

    @Value("${sni.message}")
    private String message;

    @GetMapping("/test/{id}")
    public String test(@PathVariable String id){
        return userService.test(id);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findUser(@PathVariable Long userId){
        return ResponseEntity.ok(userService.findUser(userId));
    }

    @GetMapping("/find-all-by-ids")
    public ResponseEntity<List<UserResponse>> findAllByIds(@RequestParam List<Long> ids){
        return ResponseEntity.ok(userService.findAllByIds(ids));
    }

    @GetMapping("/name/{username}")
    public ResponseEntity<UserResponse> findUserByName(@PathVariable String username){
        return ResponseEntity.ok(userService.findUserByName(username));
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserResponse> findUserByKeycloakId(@PathVariable String keycloakId){
        return ResponseEntity.ok(userService.findUserByKeycloakId(keycloakId));
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<List<UserResponse>> findUsersByNameLike(@PathVariable String username){
        return ResponseEntity.ok(userService.findUsersByNameLike(username));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/check-keycloak-and-create/{keycloakId}")
    public ResponseEntity<String> checkKeycloakAndCreateUser(@PathVariable String keycloakId){
        return ResponseEntity.ok(userService.checkKeycloakAndCreateUser(keycloakId));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponse> updateOwnUser(
            @Valid @RequestBody UserRequest request
    ){
        return ResponseEntity.ok(userService.updateOwnUser(request));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> adminUpdateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequest request
    ){
        return ResponseEntity.ok(userService.adminUpdateUser(userId, request));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteOwnUser(){
        return ResponseEntity.ok(userService.deleteOwnUser());
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteOwnUser(@PathVariable Long userId){
        return ResponseEntity.ok(userService.adminDeleteUser(userId));
    }
}
