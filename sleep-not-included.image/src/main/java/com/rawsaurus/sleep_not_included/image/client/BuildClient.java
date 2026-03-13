package com.rawsaurus.sleep_not_included.image.client;

import com.rawsaurus.sleep_not_included.image.dto.BuildResponse;
import com.rawsaurus.sleep_not_included.image.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SLEEP-NOT-INCLUDED-BUILD", configuration = FeignSecurityConfig.class)
public interface BuildClient {

    String BASE = "/api/v1/build";

    @GetMapping(BASE + "/{id}")
    ResponseEntity<BuildResponse> findById(@PathVariable Long id);

    @GetMapping(BASE + "/name/{name}")
    ResponseEntity<BuildResponse> findByName(@PathVariable String name);
}
