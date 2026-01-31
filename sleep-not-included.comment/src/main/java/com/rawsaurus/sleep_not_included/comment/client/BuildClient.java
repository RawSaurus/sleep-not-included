package com.rawsaurus.sleep_not_included.comment.client;

import com.rawsaurus.sleep_not_included.comment.dto.BuildResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SLEEP-NOT-INCLUDED-BUILD")
public interface BuildClient {

    static final String BASE = "/api/v1/build";

    @GetMapping(BASE + "/{id}")
    public ResponseEntity<BuildResponse> findById(@PathVariable Long id);
}
