package com.rawsaurus.sleep_not_included.comment.service;

import com.rawsaurus.sleep_not_included.comment.client.BuildClient;
import com.rawsaurus.sleep_not_included.comment.client.UserClient;
import com.rawsaurus.sleep_not_included.comment.config.RabbitMQConfig;
import com.rawsaurus.sleep_not_included.comment.dto.CommentRequest;
import com.rawsaurus.sleep_not_included.comment.dto.CommentResponse;
import com.rawsaurus.sleep_not_included.comment.dto.DeleteEntityEvent;
import com.rawsaurus.sleep_not_included.comment.mapper.CommentMapper;
import com.rawsaurus.sleep_not_included.comment.model.Comment;
//import com.rawsaurus.sleep_not_included.comment.model.CommentResponses;
import com.rawsaurus.sleep_not_included.comment.model.LikedComments;
import com.rawsaurus.sleep_not_included.comment.repo.CommentRepository;
import com.rawsaurus.sleep_not_included.comment.repo.LikedCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepo;
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

    public Page<CommentResponse> findAllResponses(Long parentId, Pageable pageable){
        Comment parent = commentRepo.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        return commentRepo.findAllByParent(parent, pageable)
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
        comment.setOriginal(true);

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

        var commentResToSave = commentMapper.toEntity(request);
        commentResToSave.setParent(comment);
        commentResToSave.setUserId(user.id());
        commentResToSave.setOriginal(false);

        var commentRespond = commentRepo.save(commentResToSave);
        comment.setNumOfResponses(comment.getNumOfResponses() + 1);
        commentRepo.save(comment);

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

    //implement delete responses
    //doesn't delete liked comments entity
    @Transactional
    public String deleteComment(Long id){
        var comment = commentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if(!comment.isOriginal()){
            var parent = commentRepo.findById(comment.getParent().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found"));

            parent.setNumOfResponses(parent.getNumOfResponses() - 1);
        }

        commentRepo.deleteAllByParent(comment);
        commentRepo.delete(comment);

        return "Comment deleted";
    }

    @RabbitListener(queues = RabbitMQConfig.queueName)
    @Transactional
    public void deleteCommentFromUser(DeleteEntityEvent event){
        List<Comment> commentsToDelete = commentRepo.findAllByUserId(event.id());
        List<LikedComments> likedCommentsToDelete = likedComsRepo.findAllByUserId(event.id());

        commentRepo.deleteAll(commentsToDelete);
        likedComsRepo.deleteAll(likedCommentsToDelete);

        System.out.println("comments deleted");
    }
}
