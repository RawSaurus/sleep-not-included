package com.rawsaurus.sleep_not_included.build.clients;

import com.rawsaurus.sleep_not_included.build.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@FeignClient(name="SLEEP-NOT-INCLUDED-USER")
public interface UserClient {

    @GetMapping("/api/v1/user/{userId}")
    ResponseEntity<UserResponse> findUserById(@PathVariable Long userId);
}
