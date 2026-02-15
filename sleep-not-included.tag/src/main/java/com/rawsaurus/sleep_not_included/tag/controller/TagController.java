package com.rawsaurus.sleep_not_included.tag.controller;

import com.rawsaurus.sleep_not_included.tag.dto.TagRequest;
import com.rawsaurus.sleep_not_included.tag.dto.TagResponse;
import com.rawsaurus.sleep_not_included.tag.model.BuildTags;
import com.rawsaurus.sleep_not_included.tag.model.Tag;
import com.rawsaurus.sleep_not_included.tag.model.Type;
import com.rawsaurus.sleep_not_included.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(tagService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TagResponse> findByName(@PathVariable String name){
        return ResponseEntity.ok(tagService.findByName(name));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TagResponse>> findAllByType(@PathVariable Type type){
        return ResponseEntity.ok(tagService.findAllByType(type));
    }

    @GetMapping("/builds") // change
    public ResponseEntity<List<BuildTags>> findAllBuildsByTags(@RequestBody List<Tag> tags){
        return ResponseEntity.ok(tagService.findAllBuildsByTags(tags));
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>> findAll(){
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/build/{buildId}")
    public ResponseEntity<List<TagResponse>> findAllByBuild(@PathVariable Long buildId){
        return ResponseEntity.ok(tagService.findAllByBuild(buildId));
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest request){
        return ResponseEntity.ok(tagService.createTag(request));
    }

    @PostMapping("/build/{buildId}")
    public ResponseEntity<?> addTagsToBuild(@PathVariable Long buildId, @RequestBody List<Long> tagIds){
        tagService.addTagsToBuild(buildId, tagIds);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> updateTag(@PathVariable Long id, @Valid @RequestBody TagRequest request){
        return ResponseEntity.ok(tagService.updateTag(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id){
        return ResponseEntity.ok(tagService.deleteTag(id));
    }
}
