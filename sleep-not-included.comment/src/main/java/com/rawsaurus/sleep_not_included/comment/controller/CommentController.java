package com.rawsaurus.sleep_not_included.comment.controller;


import com.rawsaurus.sleep_not_included.comment.dto.CommentRequest;
import com.rawsaurus.sleep_not_included.comment.dto.CommentResponse;
import com.rawsaurus.sleep_not_included.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/comment", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping("/responses/{commentId}")
    public ResponseEntity<Page<CommentResponse>> findAllResponses(
            @PathVariable Long commentId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "likes") String sortBy,
            @RequestParam(value = "sort-direction", defaultValue = "asc") String sortDirection
            ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        return ResponseEntity.ok(commentService.findAllResponses(commentId, pageable));
    }

    @PostMapping("/{buildId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long buildId,
            @Valid @RequestBody CommentRequest request
            ){
        return ResponseEntity.ok(commentService.createComment(buildId, request));
    }

    @PostMapping("/respond/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CommentResponse> respond(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request
    ){
        return ResponseEntity.ok(commentService.respond(commentId, request));
    }

    @PostMapping("/like/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> likeComment(
            @PathVariable Long commentId
    ){
        commentService.likeComment(commentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{buildId}/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long buildId,
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request
    ){
        return ResponseEntity.ok(commentService.updateComment(buildId, id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteComment(@PathVariable Long id){
        return ResponseEntity.ok(commentService.deleteComment(id));
    }
}
