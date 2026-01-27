package com.rawsaurus.sleep_not_included.tag.repo;

import com.rawsaurus.sleep_not_included.tag.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
