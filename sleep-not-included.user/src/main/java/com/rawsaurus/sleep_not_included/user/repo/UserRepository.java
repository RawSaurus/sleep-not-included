package com.rawsaurus.sleep_not_included.user.repo;

import com.rawsaurus.sleep_not_included.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Page<User> findAllByusernameLikeIgnoreCase(String username, Pageable pageable);
}
