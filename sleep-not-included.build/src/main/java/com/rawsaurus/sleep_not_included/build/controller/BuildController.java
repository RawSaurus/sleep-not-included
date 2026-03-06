package com.rawsaurus.sleep_not_included.build.controller;

import com.rawsaurus.sleep_not_included.build.dto.*;
import com.rawsaurus.sleep_not_included.build.service.BuildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/build", produces = MediaType.APPLICATION_JSON_VALUE)
public class BuildController {

    private final BuildService buildService;

    @GetMapping("/test-info/{userId}")
    public String testInfo(@PathVariable Long userId){
        return buildService.test(userId);
    }
    //keep for test ?
    @GetMapping("/{id}")
    public ResponseEntity<BuildResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(buildService.findById(id));
    }

//    fine
    @GetMapping("/name/{name}")
    public ResponseEntity<BuildDetailResponse> findByName(@PathVariable String name){
        return ResponseEntity.ok(buildService.findByName(name));
    }

    //detail dto not needed, just for search function
    @GetMapping("/search/{name}")
    public ResponseEntity<List<BuildResponse>> suggestSearch(@PathVariable String name){
        return ResponseEntity.ok(buildService.suggestSearch(name));
    }

    //fine update naming
    @GetMapping("/{id}/details")
    public ResponseEntity<BuildDetailResponse> findBuildDetailsById(@PathVariable Long id){
        return ResponseEntity.ok(buildService.findBuildDetailsById(id));
    }

    //fine, update naming
    @GetMapping("/all/test")
    public ResponseEntity<Page<BuildDetailResponse>> findAllTest(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(buildService.findAllBuildDetails(pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<BuildDetailResponse>> filterBuilds(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "desc") String sortDirection
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(buildService.findAllWithFilters(name, tagIds, pageable));
    }

    //to be deleted
//    @GetMapping
//    public ResponseEntity<Page<BuildResponse>> findAll(
//            @RequestParam(value = "page", defaultValue = "0") Integer page,
//            @RequestParam(value = "size", defaultValue = "10") Integer size,
//            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
//            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
//    ){
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
//        return ResponseEntity.ok(buildService.findAll(pageable));
//    }
    //to be deleted
//    @GetMapping("/logged/{userId}")
//    public ResponseEntity<Page<BuildResLoggedIn>> findAllLoggedIn(
//            @PathVariable Long userId,
//            @RequestParam(value = "page", defaultValue = "0") Integer page,
//            @RequestParam(value = "size", defaultValue = "10") Integer size,
//            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
//            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
//    ){
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
//        return ResponseEntity.ok(buildService.findAllLoggedIn(userId, pageable));
//    }

//    @GetMapping("/filters")
//    public ResponseEntity<Page<BuildDetailResponse>> findAllWithFilters(
//            @RequestParam String name,
//            @RequestBody List<TagResponse> tags,
//            @RequestParam(value = "page", defaultValue = "0") Integer page,
//            @RequestParam(value = "size", defaultValue = "10") Integer size,
//            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
//            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
//    ){
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
//        return ResponseEntity.ok(buildService.findAllWithFilters(name, tags, pageable));
//    }

    //update for new dto
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<BuildDetailResponse>> findAllFromUser(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(buildService.findAllFromUser(userId, pageable));
    }

    //update for new dto
    @GetMapping("/liked/{userId}")
    public ResponseEntity<Page<BuildDetailResponse>> findAllLikedBuilds(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(buildService.findAllLikedBuilds(userId, pageable));
    }

    //not necessary ?
    @GetMapping("/{buildId}/tags")
    public ResponseEntity<List<TagResponse>> findTagsByBuild(@PathVariable Long buildId){
        return ResponseEntity.ok(buildService.findAllTagsByBuild(buildId));
    }

    @PostMapping
    public ResponseEntity<BuildResponse> createBuild(@Valid @RequestBody BuildRequest request){
        return ResponseEntity.ok(buildService.createBuild(request));
    }

    @PostMapping("/like/{buildId}")
    public ResponseEntity<?> likeBuild(@PathVariable Long buildId){
        buildService.likeBuild(buildId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{buildId}")
    public ResponseEntity<BuildResponse> updateBuild(@PathVariable Long buildId, @Valid @RequestBody BuildRequest request){
        return ResponseEntity.ok(buildService.updateBuild(buildId, request));
    }

    @DeleteMapping("/{buildId}")
    public ResponseEntity<?> deleteBuild(@PathVariable Long buildId){
        return ResponseEntity.ok(buildService.deleteBuild(buildId));
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllFromUser(){
        buildService.deleteAllFromUser();
        return ResponseEntity.ok().build();
    }
}
