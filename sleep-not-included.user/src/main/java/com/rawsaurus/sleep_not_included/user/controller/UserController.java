package com.rawsaurus.sleep_not_included.user.controller;

import com.rawsaurus.sleep_not_included.user.dto.UserRequest;
import com.rawsaurus.sleep_not_included.user.dto.UserResponse;
import com.rawsaurus.sleep_not_included.user.model.User;
import com.rawsaurus.sleep_not_included.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/test")
    public String test(){
        RestClient restClient = RestClient.create();
        String res = restClient.get()
                .uri("http://localhost:8082/api/v1/test-info")
                .retrieve()
                .body(String.class);
        return res;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findUser(@PathVariable Long userId){
        return ResponseEntity.ok(userService.findUser(userId));
    }

    @GetMapping("/name/{username}")
    public ResponseEntity<UserResponse> findUserByName(@PathVariable String username){
        return ResponseEntity.ok(userService.findUserByName(username));
    }

    //probably hardcode pageable values
    @GetMapping("/search/{username}")
    public ResponseEntity<Page<UserResponse>> findUsersByNameLike(
            @PathVariable String username,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(userService.findUsersByNameLike(username, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId){
        return ResponseEntity.ok(userService.updateUser(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
