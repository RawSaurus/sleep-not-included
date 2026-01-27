package com.rawsaurus.sleep_not_included.game_res.repo;

import com.rawsaurus.sleep_not_included.game_res.model.GameRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameResRepository extends JpaRepository<GameRes, Long> {

}
