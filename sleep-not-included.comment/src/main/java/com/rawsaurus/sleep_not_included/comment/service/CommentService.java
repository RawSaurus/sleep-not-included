package com.rawsaurus.sleep_not_included.comment.service;

import com.rawsaurus.sleep_not_included.comment.client.BuildClient;
import com.rawsaurus.sleep_not_included.comment.client.UserClient;
import com.rawsaurus.sleep_not_included.comment.dto.CommentRequest;
import com.rawsaurus.sleep_not_included.comment.dto.CommentResponse;
import com.rawsaurus.sleep_not_included.comment.mapper.CommentMapper;
import com.rawsaurus.sleep_not_included.comment.model.Comment;
import com.rawsaurus.sleep_not_included.comment.model.CommentResponses;
import com.rawsaurus.sleep_not_included.comment.model.LikedComments;
import com.rawsaurus.sleep_not_included.comment.repo.CommentRepository;
import com.rawsaurus.sleep_not_included.comment.repo.CommentResponsesRepository;
import com.rawsaurus.sleep_not_included.comment.repo.LikedCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepo;
    private final CommentResponsesRepository comResRepo;
    private final LikedCommentRepository likedComsRepo;

    private final CommentMapper commentMapper;

    private final UserClient userClient;
    private final BuildClient buildClient;

    public String test(Long id){
        return userClient.findUser(id).getBody().toString();
    }

    public CommentResponse findById(Long id){
        return commentMapper.toResponse(
                commentRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Comment not found"))
        );
    }

    public Page<CommentResponse> findAllByBuild(Long buildId, Pageable pageable){
        var build = buildClient.findById(buildId).getBody();
        if(build == null){
            throw new EntityNotFoundException("Build not found");
        }
        return commentRepo.findAllByBuildId(build.id(), pageable)
                .map(commentMapper::toResponse);
    }

    public Page<CommentResponse> findAllByUser(Long userId, Pageable pageable){
        var user = userClient.findUser(userId).getBody();
        if(user == null){
            throw new EntityNotFoundException("User not found");
        }
        return commentRepo.findAllByUserId(user.id(), pageable)
                .map(commentMapper::toResponse);
    }

    public CommentResponse createComment(Long userId, Long buildId, CommentRequest request){
        var user = userClient.findUser(userId).getBody();
        var build = buildClient.findById(buildId).getBody();

        if(user == null){
            throw new EntityNotFoundException("User not found");
        }
        if(build == null){
            throw new EntityNotFoundException("Build not found");
        }

        Comment comment = commentMapper.toEntity(request);
        comment.setUserId(user.id());
        comment.setBuildId(build.id());

        return commentMapper.toResponse(
                commentRepo.save(comment)
        );
    }

    public CommentResponse respond(Long userId, Long commentId, CommentRequest request){
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        var user = userClient.findUser(userId).getBody();
        if(user == null){
            throw new EntityNotFoundException("User not found");
        }

        var commentRespond = commentRepo.save(
                commentMapper.toEntity(request)
        );
        var commentRespondLink = CommentResponses.builder()
                .originalCommentId(comment.getId())
                .responseId(commentRespond.getId())
                .build();
        comResRepo.save(commentRespondLink);

        return commentMapper.toResponse(commentRespond);
    }

    public void likeComment(Long userId, Long commentId){
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        var user = userClient.findUser(userId).getBody();
        if(user == null){
            throw new EntityNotFoundException("User not found");
        }
        Optional<LikedComments> likedComment = likedComsRepo.findByUserIdAndCommentId(userId, commentId);

        if(likedComment.isPresent()){
            comment.setLikes(comment.getLikes() - 1);
            likedComsRepo.delete(likedComment.get());
        }else{
            comment.setLikes(comment.getLikes() + 1);
            LikedComments likedCommentsToSave = likedComsRepo.save(LikedComments.builder()
                    .userId(userId)
                    .commentId(commentId)
                    .build());
            likedComsRepo.save(likedCommentsToSave);
        }
        commentRepo.save(comment);
    }

    public CommentResponse updateComment(Long userId, Long buildId, Long id, CommentRequest request){
        var user = userClient.findUser(userId).getBody();
        var build = buildClient.findById(buildId).getBody();

        if(user == null){
            throw new EntityNotFoundException("User not found");
        }
        if(build == null){
            throw new EntityNotFoundException("Build not found");
        }

        var comment = commentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        commentMapper.updateToEntity(request, comment);

        return commentMapper.toResponse(
                commentRepo.save(comment)
        );
    }

    public String deleteComment(Long id){
        var comment = commentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        commentRepo.delete(comment);

        return "Comment deleted";
    }
}
