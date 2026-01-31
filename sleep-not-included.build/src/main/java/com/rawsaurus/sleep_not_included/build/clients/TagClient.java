package com.rawsaurus.sleep_not_included.build.clients;

import com.rawsaurus.sleep_not_included.build.dto.TagResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="SLEEP-NOT-INCLUDED-TAG")
public interface TagClient {

    static final String BASE = "/api/v1/tag";

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> findById(@PathVariable Long id);

    @GetMapping("/name/{name}")
    public ResponseEntity<TagResponse> findByName(@PathVariable String name);

    @GetMapping("/build/{buildId}")
    public ResponseEntity<List<TagResponse>> findAllByBuild(@PathVariable Long buildId);

    @PostMapping("/build/{buildId}")
    public ResponseEntity<?> addTagsToBuild(@PathVariable Long buildId, @RequestParam List<Long> tagIds);
}
