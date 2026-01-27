package com.rawsaurus.sleep_not_included.game_res.service;

import com.rawsaurus.sleep_not_included.game_res.mapper.GameResMapper;
import com.rawsaurus.sleep_not_included.game_res.repo.GameResRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GameResService {

    private final GameResRepository gameResRepo;
    private final GameResMapper gameResMapper;

}
