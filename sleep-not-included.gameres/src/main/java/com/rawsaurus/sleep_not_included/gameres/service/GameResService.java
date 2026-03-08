package com.rawsaurus.sleep_not_included.gameres.service;

import com.rawsaurus.sleep_not_included.gameres.dto.ResSimpleRequest;
import com.rawsaurus.sleep_not_included.gameres.dto.ResSimpleResponse;
import com.rawsaurus.sleep_not_included.gameres.dto.request.ResRequest;
import com.rawsaurus.sleep_not_included.gameres.dto.response.ResResponse;
import com.rawsaurus.sleep_not_included.gameres.mapper.GameResMapper;
import com.rawsaurus.sleep_not_included.gameres.mapper.ResMapper;
import com.rawsaurus.sleep_not_included.gameres.model.*;
import com.rawsaurus.sleep_not_included.gameres.repo.GameResRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rawsaurus.sleep_not_included.gameres.model.ResType.*;

@RequiredArgsConstructor
@Service
public class GameResService {

    private final GameResRepository gameResRepo;
//    private final RecipoRepo recipeRepo;
    private final GameResMapper gameResMapper;
    private final ResMapper resMapper;

    public ResResponse testGet(Long id){
        return resMapper.toResponse(
                gameResRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Resource not found"))
        );
    }

//    public void testSave(){
//        Recipe r1 = Recipe.builder()
//                .inp(List.of("in1", "in2"))
//                .middle("Middle")
//                .out(List.of("out1", "out2"))
//                .build();
//        Recipe r2 = Recipe.builder()
//                .inp(List.of("in1", "in2"))
//                .middle("Middle")
//                .out(List.of("out1", "out2"))
//                .build();
//        Recipe r3 = Recipe.builder()
//                .inp(List.of("in1", "in2"))
//                .middle("Middle")
//                .out(List.of("out1", "out2"))
//                .build();
//
//        recipeRepo.save(r1);
//    }

    public void testSave(){
        Recipe r1 = Recipe.builder()
                .inp(List.of("in1", "in2"))
                .middle("Middle")
                .out(List.of("out1", "out2"))
                .build();
        Recipe r2 = Recipe.builder()
                .inp(List.of("in1", "in2"))
                .middle("Middle")
                .out(List.of("out1", "out2"))
                .build();
        Recipe r3 = Recipe.builder()
                .inp(List.of("in1", "in2"))
                .middle("Middle")
                .out(List.of("out1", "out2"))
                .build();
//        Recipe r1 = new Recipe("in", "mid", "out");
//        Recipe r2 = new Recipe("in", "mid", "out");
//        Recipe r3 = new Recipe("in", "mid", "out");
        Map<String, List<Recipe>> map = new HashMap<>();
        map.put("consumes", List.of(r1, r2));
        map.put("produces", List.of(r2, r3));
        Map<String, List<Recipe>> map1 = new HashMap<>();
        map1.put("consumes", List.of(r3, r2));
        map1.put("produces", List.of(r1, r2));

        Building building = Building.builder()
                .name("building")
                .description("test")
                .resType(BUILDING)
                .imageUrl("ririr")
                .size("2x2")
                .thermalConductivity(2.2)
                .power(new ValueUnit(20, "pow"))
                .heat(new ValueUnit(10, "heat"))
                .recipes(map)
                .build();

        Element element = Element.builder()
                .name("element")
                .description("test 1")
                .imageUrl("ririsese")
                .resType(ELEMENT)
                .thermalConductivity(4.4)
                .hardness(10)
                .molarMass(4.3)
                .lightAbsorptionFactor(2)
                .state(State.LIQUID)
                .recipes(map1)
                .build();

        gameResRepo.save(element);
        gameResRepo.save(building);
    }

//    public ResResponse testRequest(ResResponse req){
//        GameRes res = resMapper.t
//    }

    public ResSimpleResponse findSimpleById(Long id) {
        return gameResMapper.toResponse(
                gameResRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Resource not found: " + id))
        );
    }

    public ResSimpleResponse findSimpleByName(String name) {
        return gameResMapper.toResponse(
                gameResRepo.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Resource not found: " + name))
        );
    }

    public List<ResSimpleResponse> findAllSimpleByType(ResType type) {
        return gameResRepo.findAllByResType(type)
                .stream()
                .map(gameResMapper::toResponse)
                .toList();
    }

    public ResResponse findById(Long id) {
        return resMapper.toResponse(
                gameResRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Resource not found: " + id))
        );
    }

    public ResResponse findByName(String name) {
        return resMapper.toResponse(
                gameResRepo.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Resource not found: " + name))
        );
    }

    public List<ResResponse> findAllByType(ResType type) {
        return gameResRepo.findAllByResType(type)
                .stream()
                .map(resMapper::toResponse)
                .toList();
    }

    public ResResponse save(ResRequest request) {
        return resMapper.toResponse(
                gameResRepo.save(resMapper.toEntity(request))
        );
    }

    public ResResponse update(Long id, ResRequest request) {
        GameRes existing = gameResRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found: " + id));
        GameRes updated = resMapper.toEntity(request);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        return resMapper.toResponse(gameResRepo.save(updated));
    }

    public void delete(Long id) {
        if (!gameResRepo.existsById(id)) {
            throw new EntityNotFoundException("Resource not found: " + id);
        }
        gameResRepo.deleteById(id);
    }

}
