package com.rawsaurus.sleep_not_included.user.repo;

import com.rawsaurus.sleep_not_included.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("""
         SELECT u FROM User u
         WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))
         ORDER BY CASE
            WHEN LOWER(u.username) LIKE LOWER(CONCAT(:username, '%')) THEN 0
            ELSE 1
            END,
            LENGTH(u.username)
""")
    List<User> searchUsers(@Param("username") String username, Pageable pageable);

    Page<User> findAllByusernameLikeIgnoreCase(String username, Pageable pageable);
}
