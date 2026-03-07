package com.rawsaurus.sleep_not_included.gameres.controller;

import com.rawsaurus.sleep_not_included.gameres.dto.GameResRequest;
import com.rawsaurus.sleep_not_included.gameres.dto.GameResResponse;
import com.rawsaurus.sleep_not_included.gameres.dto.ResResponse;
import com.rawsaurus.sleep_not_included.gameres.service.GameResService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/gameres", produces = MediaType.APPLICATION_JSON_VALUE)
public class GameResController {

    private final GameResService gameResService;

    @Value("sni.message")
    private String message;

    @GetMapping("/test/get/{id}")
    public ResponseEntity<ResResponse> testGet(@PathVariable Long id){
        return ResponseEntity.ok(gameResService.testGet(id));
    }

    @GetMapping("/test/save")
    public ResponseEntity<?> testSave(){
        gameResService.testSave();
        return ResponseEntity.ok().build();
    }

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
