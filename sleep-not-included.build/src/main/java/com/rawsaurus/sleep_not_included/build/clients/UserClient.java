package com.rawsaurus.sleep_not_included.build.clients;

import com.rawsaurus.sleep_not_included.build.dto.UserResponse;
import com.rawsaurus.sleep_not_included.build.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@FeignClient(name="SLEEP-NOT-INCLUDED-USER", configuration = FeignSecurityConfig.class)
public interface UserClient {

    String BASE = "/api/v1/user";

    @GetMapping(BASE + "/{userId}")
    ResponseEntity<UserResponse> findUserById(@PathVariable Long userId);

    @GetMapping(BASE + "/find-all-by-ids")
    ResponseEntity<List<UserResponse>> findAllByIds(@RequestParam List<Long> ids);
}
