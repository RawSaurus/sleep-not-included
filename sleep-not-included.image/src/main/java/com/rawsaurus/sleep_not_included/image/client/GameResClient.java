package com.rawsaurus.sleep_not_included.image.client;

import com.rawsaurus.sleep_not_included.image.dto.GameResResponse;
import com.rawsaurus.sleep_not_included.image.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SLEEP-NOT-INCLUDED-GAMERES", configuration = FeignSecurityConfig.class)
public interface GameResClient {

    String BASE = "/api/v1/res";

    @GetMapping(BASE + "/{id}")
    ResponseEntity<GameResResponse> findById(@PathVariable Long id);

    @GetMapping(BASE + "/name/{name}")
    ResponseEntity<GameResResponse> findByName(@PathVariable String name);
}
