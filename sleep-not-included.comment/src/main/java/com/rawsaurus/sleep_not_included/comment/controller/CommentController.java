package com.rawsaurus.sleep_not_included.comment.controller;


import com.rawsaurus.sleep_not_included.comment.dto.CommentRequest;
import com.rawsaurus.sleep_not_included.comment.dto.CommentResponse;
import com.rawsaurus.sleep_not_included.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/test/{userId}")
    public ResponseEntity<String> test(@PathVariable Long userId){
        return ResponseEntity.ok(commentService.test(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(commentService.findById(id));
    }

    @GetMapping("/build/{buildId}")
    public ResponseEntity<Page<CommentResponse>> findAllByBuild(
            @PathVariable Long buildId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "likes") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(commentService.findAllByBuild(buildId, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommentResponse>> findAllByUser(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "likes") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(commentService.findAllByUser(userId, pageable));
    }

    @PostMapping("/{userId}/{buildId}")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long userId,
            @PathVariable Long buildId,
            @RequestBody CommentRequest request
            ){
        return ResponseEntity.ok(commentService.createComment(userId, buildId, request));
    }

    @PutMapping("/{userId}/{buildId}/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long userId,
            @PathVariable Long buildId,
            @PathVariable Long id,
            @RequestBody CommentRequest request
    ){
        return ResponseEntity.ok(commentService.updateComment(userId, buildId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id){
        return ResponseEntity.ok(commentService.deleteComment(id));
    }
}
