package com.rawsaurus.sleep_not_included.image.client;

import com.rawsaurus.sleep_not_included.image.dto.GameResResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SLEEP-NOT-INCLUDED-GAME_RES")
public interface GameResClient {

    String ROOT = "/api/v1/res";

    @GetMapping(ROOT + "/{id}")
    public ResponseEntity<GameResResponse> findById(@PathVariable Long id);

    @GetMapping(ROOT + "/name/{name}")
    public ResponseEntity<GameResResponse> findByName(@PathVariable String name);
}
