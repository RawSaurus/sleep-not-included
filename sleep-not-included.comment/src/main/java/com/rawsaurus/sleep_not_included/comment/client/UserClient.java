package com.rawsaurus.sleep_not_included.comment.client;

import com.rawsaurus.sleep_not_included.comment.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SLEEP-NOT-INCLUDED-USER")
public interface UserClient {

    @GetMapping("/api/v1/user/{userId}")
    public ResponseEntity<UserResponse> findUser(@PathVariable Long userId);

}
