package com.rawsaurus.sleep_not_included.comment.service;

import com.rawsaurus.sleep_not_included.comment.client.BuildClient;
import com.rawsaurus.sleep_not_included.comment.client.UserClient;
import com.rawsaurus.sleep_not_included.comment.config.RabbitMQConfig;
import com.rawsaurus.sleep_not_included.comment.dto.CommentRequest;
import com.rawsaurus.sleep_not_included.comment.dto.CommentResponse;
import com.rawsaurus.sleep_not_included.comment.dto.DeleteEntityEvent;
import com.rawsaurus.sleep_not_included.comment.dto.UserResponse;
import com.rawsaurus.sleep_not_included.comment.handler.ActionNotAllowed;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
//        return commentMapper.toResponse(
//                commentRepo.findById(id)
//                        .orElseThrow(() -> new EntityNotFoundException("Comment not found"))
//        );
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        return toCommentResponse(comment);
    }

    public Page<CommentResponse> findAllByBuild(Long buildId, Pageable pageable){
//        var build = buildClient.findById(buildId).getBody();
//        if(build == null){
//            throw new EntityNotFoundException("Build not found");
//        }
//        return commentRepo.findAllByBuildId(build.id(), pageable)
//                .map(commentMapper::toResponse);
        var build = buildClient.findById(buildId).getBody();
        if(build == null){
            throw new EntityNotFoundException("Build not found");
        }

        Page<Comment> comments = commentRepo.findAllByBuildId(build.id(), pageable);
        List<Comment> content = comments.getContent();

        if(content.isEmpty()){
            return Page.empty(pageable);
        }

        Long currentLoggedUserId = resolveUserId();
        List<CommentResponse> responses = content.stream()
                .map(c -> toCommentResponse(c, currentLoggedUserId))
                .toList();

        return new PageImpl<>(responses, pageable, comments.getTotalElements());
    }

    public Page<CommentResponse> findAllByUser(Long userId, Pageable pageable){
//        var user = userClient.findUser(userId).getBody();
//        if(user == null){
//            throw new EntityNotFoundException("User not found");
//        }
//        return commentRepo.findAllByUserId(user.id(), pageable)
//                .map(commentMapper::toResponse);
        var user = userClient.findUser(userId).getBody();
        if(user == null){
            throw new EntityNotFoundException("User not found");
        }
        Page<Comment> comments = commentRepo.findAllByUserId(user.id(), pageable);
        List<Comment> content = comments.getContent();

        if(content.isEmpty()){
            return Page.empty(pageable);
        }
        Long currentLoggedUserId = resolveUserId();
        List<CommentResponse> responses = content.stream()
                .map(c -> toCommentResponse(c, currentLoggedUserId))
                .toList();

        return new PageImpl<>(responses, pageable, comments.getTotalElements());
    }

    public Page<CommentResponse> findAllResponses(Long parentId, Pageable pageable){
//        Comment parent = commentRepo.findById(parentId)
//                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
//        return commentRepo.findAllByParent(parent, pageable)
//                .map(commentMapper::toResponse);
        Comment parent = commentRepo.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        Page<Comment> comments = commentRepo.findAllByParent(parent, pageable);
        List<Comment> content = comments.getContent();

        if (content.isEmpty()) {
            return Page.empty(pageable);
        }

        Long currentLoggedUserId = resolveUserId();
        List<CommentResponse> responses = content.stream()
                .map(c -> toCommentResponse(c, currentLoggedUserId))
                .toList();
        return new PageImpl<>(responses, pageable, comments.getTotalElements());
    }

    public CommentResponse createComment(Long buildId, CommentRequest request){
        var user = resolveUser();

        var build = buildClient.findById(buildId).getBody();

        if(user == null){
            throw new EntityNotFoundException("User not found");
        }

        if(build == null){
            throw new EntityNotFoundException("Build not found");
        }

        Comment comment = commentMapper.toEntity(request);

        comment.setUserId(user.id());
        comment.setUsername(user.username());
        comment.setBuildId(build.id());
        comment.setOriginal(true);

        return commentMapper.toResponse(
                commentRepo.save(comment)
        );
    }

    public CommentResponse respond(Long commentId, CommentRequest request){
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        var user = resolveUser();
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

    public void likeComment(Long commentId){
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        var user = resolveUser();
        if(user == null){
            throw new EntityNotFoundException("User not found");
        }
        Optional<LikedComments> likedComment = likedComsRepo.findByUserIdAndCommentId(user.id(), commentId);

        if(likedComment.isPresent()){
            comment.setLikes(comment.getLikes() - 1);
            likedComsRepo.delete(likedComment.get());
        }else{
            comment.setLikes(comment.getLikes() + 1);
            LikedComments likedCommentsToSave = likedComsRepo.save(LikedComments.builder()
                    .userId(user.id())
                    .commentId(commentId)
                    .build());
            likedComsRepo.save(likedCommentsToSave);
        }
        commentRepo.save(comment);
    }

    public CommentResponse updateComment(Long buildId, Long id, CommentRequest request){
        var user = resolveUser();
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

    @Transactional
    public String deleteComment(Long id){
        var comment = commentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        var user = resolveUser();
        if(!comment.getUserId().equals(user.id())){
            throw new ActionNotAllowed("Only creator can delete the comment");
        }
        List<LikedComments> likedCommentsToDelete = likedComsRepo.findAllByCommentId(comment.getId());

        if(!comment.isOriginal()){
            var parent = commentRepo.findById(comment.getParent().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found"));

            parent.setNumOfResponses(parent.getNumOfResponses() - 1);
        }

        commentRepo.deleteAllByParent(comment);
        commentRepo.delete(comment);
        likedComsRepo.deleteAll(likedCommentsToDelete);

        return "Comment deleted";
    }

    @RabbitListener(queues = RabbitMQConfig.COMMENT_USER_DELETED_QUEUE)
    @Transactional
    public void deleteCommentFromUser(DeleteEntityEvent event){
        List<Comment> commentsToDelete = commentRepo.findAllByUserId(event.id());
        List<LikedComments> likedCommentsToDelete = likedComsRepo.findAllByUserId(event.id());

        commentRepo.deleteAll(commentsToDelete);
        likedComsRepo.deleteAll(likedCommentsToDelete);
    }

    @RabbitListener(queues = RabbitMQConfig.COMMENT_BUILD_DELETED_QUEUE)
    @Transactional
    public void deleteCommentFromBuild(DeleteEntityEvent event){
        List<Comment> commentsToDelete = commentRepo.findAllByBuildId(event.id());
        List<LikedComments> likedCommentsToDelete = likedComsRepo.findAllByCommentIdIn(
                commentsToDelete.stream().map(c -> c.getId()).toList()
        );

        commentRepo.deleteAll(commentsToDelete);
        likedComsRepo.deleteAll(likedCommentsToDelete);
    }

    private CommentResponse toCommentResponse(Comment comment){
        Long userId = resolveUserId();
        Boolean isLiked = userId != null
                ? likedComsRepo.existsByUserIdAndCommentId(userId, comment.getId())
                : null;
        return new CommentResponse(
                comment.getId(),
                comment.getBody(),
                comment.getUserId().toString(),
                comment.getUsername(),
                comment.getLikes(),
                comment.getCreatedAt(),
                comment.getNumOfResponses(),
                isLiked
        );
    }

    private CommentResponse toCommentResponse(Comment comment, Long userId){
        Boolean isLiked = userId != null
                ? likedComsRepo.existsByUserIdAndCommentId(userId, comment.getId())
                : null;
        return new CommentResponse(
                comment.getId(),
                comment.getBody(),
                comment.getUserId().toString(),
                comment.getUsername(),
                comment.getLikes(),
                comment.getCreatedAt(),
                comment.getNumOfResponses(),
                isLiked
        );
    }

    private UserResponse resolveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getSubject(); // "sub" claim
        var user = userClient.findUserByKeycloakId(keycloakId).getBody();
        return user != null ? user : null;
    }

    private Long resolveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getSubject(); // "sub" claim
        var user = userClient.findUserByKeycloakId(keycloakId).getBody();
        return user != null ? user.id() : null;
    }
}
