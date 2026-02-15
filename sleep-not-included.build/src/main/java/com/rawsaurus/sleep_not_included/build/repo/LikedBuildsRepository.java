package com.rawsaurus.sleep_not_included.build.repo;

import com.rawsaurus.sleep_not_included.build.model.LikedBuilds;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikedBuildsRepository extends JpaRepository<LikedBuilds, Long> {

    boolean existsByUserIdAndBuildId(Long userId, Long buildId);

    Optional<LikedBuilds> findByUserIdAndBuildId(Long userId, Long buildId);
    List<LikedBuilds> findAllByUserId(Long userId);
    List<LikedBuilds> findAllByBuildId(Long buildId);
}
