package com.rawsaurus.sleep_not_included.comment.client;

import com.rawsaurus.sleep_not_included.comment.dto.BuildResponse;
import com.rawsaurus.sleep_not_included.comment.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SLEEP-NOT-INCLUDED-BUILD", configuration = FeignSecurityConfig.class)
public interface BuildClient {

    String BASE = "/api/v1/build";

    @GetMapping(BASE + "/{id}")
    ResponseEntity<BuildResponse> findById(@PathVariable Long id);
}
