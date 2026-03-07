package com.rawsaurus.sleep_not_included.gameres.service;

import com.rawsaurus.sleep_not_included.gameres.dto.GameResRequest;
import com.rawsaurus.sleep_not_included.gameres.dto.GameResResponse;
import com.rawsaurus.sleep_not_included.gameres.dto.ResResponse;
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
