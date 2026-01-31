package com.rawsaurus.sleep_not_included.build.controller;

import com.rawsaurus.sleep_not_included.build.dto.BuildRequest;
import com.rawsaurus.sleep_not_included.build.dto.BuildResponse;
import com.rawsaurus.sleep_not_included.build.service.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/build")
public class BuildController {

    private final BuildService buildService;

    @GetMapping("/test-info/{userId}")
    public String testInfo(@PathVariable Long userId){
        return buildService.test(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(buildService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<BuildResponse> findByName(@PathVariable String name){
        return ResponseEntity.ok(buildService.findByName(name));
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<Page<BuildResponse>> suggestSearch(@PathVariable String name){
        return ResponseEntity.ok(buildService.suggestSearch(name));
    }

    @GetMapping
    public ResponseEntity<Page<BuildResponse>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(buildService.findAll(pageable));
    }

//    @GetMapping("/filters")
//    public ResponseEntity<Page<BuildResponse>> findAllWithFilters(
//            @RequestBody Set<Long> tags,  // temp type
//            @RequestBody Set<Long> dlc,
//            @RequestParam(value = "page", defaultValue = "0") Integer page,
//            @RequestParam(value = "size", defaultValue = "10") Integer size,
//            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
//            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
//    ){
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
//        return ResponseEntity.ok(buildService.findAllWithFilters(tags, dlc, pageable));
//    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<BuildResponse>> findAllFromUser(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(buildService.findAllFromUser(userId, pageable));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<BuildResponse> createBuild(@PathVariable Long userId, @RequestBody BuildRequest request){
        return ResponseEntity.ok(buildService.createBuild(userId, request));
    }

    @PostMapping("/like/{userId}/{buildId}")
    public ResponseEntity<?> likeBuild(@PathVariable Long userId, @PathVariable Long buildId){
        buildService.likeBuild(userId, buildId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/{buildId}")
    public ResponseEntity<BuildResponse> updateBuild(@PathVariable Long userId, @PathVariable Long buildId, @RequestBody BuildRequest request){
        return ResponseEntity.ok(buildService.updateBuild(userId, buildId, request));
    }

    @DeleteMapping("/{userId}/{buildId}")
    public ResponseEntity<?> deleteBuild(@PathVariable Long userId, @PathVariable Long buildId){
        return ResponseEntity.ok(buildService.deleteBuild(userId, buildId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteAllFromUser(@PathVariable Long userId){
        buildService.deleteAllFromUser(userId);
        return ResponseEntity.ok().build();
    }
}
