package com.rawsaurus.sleep_not_included.image.client;

import com.rawsaurus.sleep_not_included.image.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="SLEEP-NOT-INCLUDED-USER")
public interface UserClient {

    @GetMapping("/api/v1/user/{userId}")
    ResponseEntity<UserResponse> findUserById(@PathVariable Long userId);

    @GetMapping("/api/v1/user/name/{username}")
    ResponseEntity<UserResponse> findUserByName(@PathVariable String username);
}
