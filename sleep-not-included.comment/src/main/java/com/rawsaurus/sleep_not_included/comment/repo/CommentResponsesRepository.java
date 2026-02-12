package com.rawsaurus.sleep_not_included.comment.repo;

import com.rawsaurus.sleep_not_included.comment.model.CommentResponses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentResponsesRepository extends JpaRepository<CommentResponses, Long> {
}
