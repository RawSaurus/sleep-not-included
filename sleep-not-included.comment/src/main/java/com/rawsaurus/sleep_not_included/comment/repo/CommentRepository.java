package com.rawsaurus.sleep_not_included.comment.repo;

import com.rawsaurus.sleep_not_included.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
