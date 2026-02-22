package com.rawsaurus.sleep_not_included.build.repo;

import com.rawsaurus.sleep_not_included.build.model.Build;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BuildRepository extends JpaRepository<Build, Long> {

    Optional<Build> findByName(String name);

    List<Build> findAllByCreatorId(Long creatorId);

    Page<Build> findAllByNameLikeIgnoreCase(String name, Pageable pageable);
//    Page<Build> findAllByDlcIdAndTagsId(Set<Long> dlcId, Set<Long> tagsId, Pageable pageable);
    Page<Build> findAllByCreatorId(Long creatorId, Pageable pageable);

    @Query("""
         SELECT b FROM Build b
         WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))
         ORDER BY CASE
            WHEN LOWER(b.name) LIKE LOWER(CONCAT(:name, '%')) THEN 0
            ELSE 1
            END,
            LENGTH(b.name)
""")
    List<Build> searchBuilds(@Param("name") String name, Pageable pageable);
}
