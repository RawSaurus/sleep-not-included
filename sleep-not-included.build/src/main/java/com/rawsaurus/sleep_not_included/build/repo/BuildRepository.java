package com.rawsaurus.sleep_not_included.build.repo;

import com.rawsaurus.sleep_not_included.build.model.Build;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BuildRepository extends JpaRepository<Build, Long> {

    Optional<Build> findByName(String name);

    List<Build> findAllByCreatorId(Long creatorId);

    Page<Build> findAllByNameLikeIgnoreCase(String name, Pageable pageable);
    Page<Build> findAllByDlcIdAndTagsId(Set<Long> dlcId, Set<Long> tagsId, Pageable pageable);
    Page<Build> findAllByCreatorId(Long creatorId, Pageable pageable);
}
