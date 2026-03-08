package com.rawsaurus.sleep_not_included.gameres.controller;

import com.rawsaurus.sleep_not_included.gameres.dto.ResSimpleRequest;
import com.rawsaurus.sleep_not_included.gameres.dto.ResSimpleResponse;
import com.rawsaurus.sleep_not_included.gameres.dto.request.ResRequest;
import com.rawsaurus.sleep_not_included.gameres.dto.response.ResResponse;
import com.rawsaurus.sleep_not_included.gameres.model.ResType;
import com.rawsaurus.sleep_not_included.gameres.service.GameResService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/simple/{id}")
    public ResponseEntity<ResSimpleResponse> findSimpleById(@PathVariable Long id) {
        return ResponseEntity.ok(gameResService.findSimpleById(id));
    }

    @GetMapping("/simple/name/{name}")
    public ResponseEntity<ResSimpleResponse> findSimpleByName(@PathVariable String name) {
        return ResponseEntity.ok(gameResService.findSimpleByName(name));
    }

    @GetMapping("/simple/type/{type}")
    public ResponseEntity<List<ResSimpleResponse>> findAllSimpleByType(@PathVariable ResType type) {
        return ResponseEntity.ok(gameResService.findAllSimpleByType(type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(gameResService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ResResponse> findByName(@PathVariable String name){
        return ResponseEntity.ok(gameResService.findByName(name));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ResResponse>> findAllByType(@PathVariable ResType type){
        return ResponseEntity.ok(gameResService.findAllByType(type));
    }

    @PostMapping
    public ResponseEntity<ResResponse> save(@Valid @RequestBody ResRequest request) {
        return ResponseEntity.ok(gameResService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResResponse> update(@PathVariable Long id, @Valid @RequestBody ResRequest request) {
        return ResponseEntity.ok(gameResService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameResService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
