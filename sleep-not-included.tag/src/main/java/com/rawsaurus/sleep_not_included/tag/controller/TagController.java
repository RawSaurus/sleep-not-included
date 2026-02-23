package com.rawsaurus.sleep_not_included.tag.controller;

import com.rawsaurus.sleep_not_included.tag.dto.TagRequest;
import com.rawsaurus.sleep_not_included.tag.dto.TagResponse;
import com.rawsaurus.sleep_not_included.tag.model.Type;
import com.rawsaurus.sleep_not_included.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<Page<TagResponse>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "name") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(tagService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest request){
        return ResponseEntity.ok(tagService.createTag(request));
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
