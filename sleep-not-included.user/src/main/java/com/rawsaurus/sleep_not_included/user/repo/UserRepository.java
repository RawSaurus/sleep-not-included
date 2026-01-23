package com.rawsaurus.sleep_not_included.user.repo;

import com.rawsaurus.sleep_not_included.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
