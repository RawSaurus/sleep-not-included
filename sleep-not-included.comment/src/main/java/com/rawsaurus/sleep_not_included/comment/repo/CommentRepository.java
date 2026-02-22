package com.rawsaurus.sleep_not_included.comment.repo;

import com.rawsaurus.sleep_not_included.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByUserId(Long userId);
    List<Comment> findAllByBuildId(Long buildId);

    Page<Comment> findAllByBuildId(Long buildId, Pageable pageable);
    Page<Comment> findAllByUserId(Long userId, Pageable pageable);
    Page<Comment> findAllByParent(Comment parent, Pageable pageable);

    int countCommentByParent(Comment parent);

    void deleteAllByParent(Comment parent);
}
