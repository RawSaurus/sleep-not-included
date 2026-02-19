package com.rawsaurus.sleep_not_included.image.client;

import com.rawsaurus.sleep_not_included.image.dto.BuildResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SLEEP-NOT-INCLUDED-BUILD")
public interface BuildClient {

    String ROOT = "/api/v1/build";

    @GetMapping(ROOT + "/{id}")
    ResponseEntity<BuildResponse> findById(@PathVariable Long id);

    @GetMapping(ROOT + "/name/{name}")
    ResponseEntity<BuildResponse> findByName(@PathVariable String name);
}
