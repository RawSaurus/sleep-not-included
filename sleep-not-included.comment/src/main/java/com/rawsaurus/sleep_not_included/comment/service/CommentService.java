package com.rawsaurus.sleep_not_included.comment.service;

import com.rawsaurus.sleep_not_included.comment.dto.CommentResponse;
import com.rawsaurus.sleep_not_included.comment.mapper.CommentMapper;
import com.rawsaurus.sleep_not_included.comment.repo.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepo;
    private final CommentMapper commentMapper;

    public CommentResponse findById(Long id){
        return commentMapper.toResponse(
                commentRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Comment not found"))
        );
    }

}
