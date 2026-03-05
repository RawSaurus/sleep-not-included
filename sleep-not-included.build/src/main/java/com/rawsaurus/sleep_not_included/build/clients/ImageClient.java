package com.rawsaurus.sleep_not_included.build.clients;

import com.rawsaurus.sleep_not_included.build.dto.ImageResponse;
import com.rawsaurus.sleep_not_included.build.dto.UserResponse;
import com.rawsaurus.sleep_not_included.build.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="SLEEP-NOT-INCLUDED-IMAGE", configuration = FeignSecurityConfig.class)
public interface ImageClient {

    String BASE = "/api/v1/image";

    @GetMapping(BASE + "/{id}")
    ResponseEntity<ImageResponse> findById(@PathVariable Long id);

    @GetMapping(BASE + "/owner/{ownerService}/{ownerId}")
    ResponseEntity<List<ImageResponse>> findByOwner(@PathVariable String ownerService, @PathVariable Long ownerId);

    @GetMapping(BASE + "/owner/{ownerService}/batch")
    ResponseEntity<List<ImageResponse>> findAllByOwnerIds(@PathVariable String ownerService, @RequestParam List<Long> ownerIds);

    @GetMapping("/owner/{ownerService}/{ownerId}/url")
    ResponseEntity<List<String>> findUrlByOwner(@PathVariable String ownerService, @PathVariable Long ownerId);

    @GetMapping("/owner/{ownerService}/batch/url")
    ResponseEntity<List<String>> findAllUrlByOwnerIds(@PathVariable String ownerService, @RequestParam List<Long> ownerIds);
}
