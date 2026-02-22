package com.rawsaurus.sleep_not_included.gameres.controller;

import com.rawsaurus.sleep_not_included.gameres.dto.GameResRequest;
import com.rawsaurus.sleep_not_included.gameres.dto.GameResResponse;
import com.rawsaurus.sleep_not_included.gameres.service.GameResService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/res")
public class GameResController {

    private final GameResService gameResService;

    @Value("sni.message")
    private String message;

    @GetMapping("/{id}")
    public ResponseEntity<GameResResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(gameResService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<GameResResponse> findByName(@PathVariable String name){
        return ResponseEntity.ok(gameResService.findByName(name));
    }

    @PostMapping
    public ResponseEntity<GameResResponse> createRes(@RequestBody GameResRequest request){
        return ResponseEntity.ok(gameResService.createRes(request));
    }

}
