package com.rawsaurus.sleep_not_included.gameres.repo;

import com.rawsaurus.sleep_not_included.gameres.model.GameRes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameResRepository extends JpaRepository<GameRes, Long> {

    Optional<GameRes> findByName(String name);
}
