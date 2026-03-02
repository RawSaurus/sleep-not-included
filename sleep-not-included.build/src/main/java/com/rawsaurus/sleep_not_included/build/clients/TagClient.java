package com.rawsaurus.sleep_not_included.build.clients;

import com.rawsaurus.sleep_not_included.build.dto.TagResponse;
import com.rawsaurus.sleep_not_included.build.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="SLEEP-NOT-INCLUDED-TAG", configuration = FeignSecurityConfig.class)
public interface TagClient {

    String BASE = "/api/v1/tag";

    @GetMapping(BASE + "/{id}")
    ResponseEntity<TagResponse> findById(@PathVariable Long id);

    @GetMapping(BASE + "/name/{name}")
    ResponseEntity<TagResponse> findByName(@PathVariable String name);

    @GetMapping(BASE + "/build/{buildId}")
    ResponseEntity<List<TagResponse>> findAllByBuild(@PathVariable Long buildId);

    @GetMapping(BASE + "/find-all-by-ids")
    ResponseEntity<List<TagResponse>> findAllByIds(@RequestParam List<Long> ids);

    @PostMapping(BASE + "/build/{buildId}")
    ResponseEntity<?> addTagsToBuild(@PathVariable Long buildId, @RequestParam List<Long> tagIds);
}
