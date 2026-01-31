package com.rawsaurus.sleep_not_included.tag.repo;

import com.rawsaurus.sleep_not_included.tag.model.BuildTags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildTagsRepository extends JpaRepository<BuildTags, Long> {

    Optional<BuildTags> findBybuildIdAndTagId(Long buildId, Long tagId);

    List<BuildTags> findAllByTagId(Long tagId);
    List<BuildTags> findAllByBuildId(Long buildId);
}
