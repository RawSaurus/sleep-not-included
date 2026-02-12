package com.rawsaurus.sleep_not_included.comment.repo;

import com.rawsaurus.sleep_not_included.comment.model.LikedComments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikedCommentRepository extends JpaRepository<LikedComments, Long> {

    Optional<LikedComments> findByUserIdAndCommentId(Long userId, Long commentId);
}
