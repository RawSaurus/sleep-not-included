package com.rawsaurus.sleep_not_included.image.client;

import com.rawsaurus.sleep_not_included.image.dto.UserResponse;
import com.rawsaurus.sleep_not_included.image.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="SLEEP-NOT-INCLUDED-USER", configuration = FeignSecurityConfig.class)
public interface UserClient {

    String BASE = "/api/v1/user";

    @GetMapping( BASE + "/{userId}")
    ResponseEntity<UserResponse> findUserById(@PathVariable Long userId);

    @GetMapping(BASE + "/name/{username}")
    ResponseEntity<UserResponse> findUserByName(@PathVariable String username);
}
