package com.rawsaurus.sleep_not_included.gameres.service;

import com.rawsaurus.sleep_not_included.gameres.dto.GameResRequest;
import com.rawsaurus.sleep_not_included.gameres.dto.GameResResponse;
import com.rawsaurus.sleep_not_included.gameres.mapper.GameResMapper;
import com.rawsaurus.sleep_not_included.gameres.repo.GameResRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GameResService {

    private final GameResRepository gameResRepo;
    private final GameResMapper gameResMapper;

    public GameResResponse findById(Long id){
        return gameResMapper.toResponse(
                gameResRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Resource not found"))
        );
    }

    public GameResResponse findByName(String name){
        return gameResMapper.toResponse(
                gameResRepo.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Res not found"))
        );
    }

    public GameResResponse createRes(GameResRequest request){
        return gameResMapper.toResponse(
                gameResRepo.save(
                        gameResMapper.toEntity(request)
                )
        );
    }
}
