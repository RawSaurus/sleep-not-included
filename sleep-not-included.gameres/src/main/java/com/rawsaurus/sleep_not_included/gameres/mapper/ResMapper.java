package com.rawsaurus.sleep_not_included.gameres.mapper;

import com.rawsaurus.sleep_not_included.gameres.dto.*;
import com.rawsaurus.sleep_not_included.gameres.dto.request.*;
import com.rawsaurus.sleep_not_included.gameres.dto.response.*;
import com.rawsaurus.sleep_not_included.gameres.handler.ActionNotAllowed;
import com.rawsaurus.sleep_not_included.gameres.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ResMapper {

    // ─── toResponse (polymorphic dispatch) ───────────────────────────────────

    public ResResponse toResponse(GameRes res) {
        return switch (res.getResType()) {
            case ELEMENT  -> toElementResponse((Element) res);
            case BUILDING -> toBuildingResponse((Building) res);
            case CRITTER  -> toCritterResponse((Critter) res);
            case EGG      -> toEggResponse((Egg) res);
            case PLANT    -> toPlantResponse((Plant) res);
            case SEED     -> toSeedResponse((Seed) res);
            case FOOD     -> toFoodResponse((Food) res);
            case PLANET   -> toPlanetResponse((Planet) res);
            case ARTIFACT -> toArtifactResponse((Artifact) res);
            case COMET    -> toCometResponse((Comet) res);
            case GEYSER   -> toGeyserResponse((Geyser) res);
            case MEDICINE -> toMedicineResponse((Medicine) res);
            case DISEASE  -> toDiseaseResponse((Disease) res);
            case PATHOGEN -> toPathogenResponse((Pathogen) res);
            case MISC     -> toOtherResponse((Other) res);
        };
    }

    // ─── toEntity (polymorphic dispatch) ─────────────────────────────────────

    public GameRes toEntity(ResRequest request) {
        return switch (request.getResType()) {
            case ELEMENT  -> toElementEntity((ElementRequest) request);
            case BUILDING -> toBuildingEntity((BuildingRequest) request);
            case CRITTER  -> toCritterEntity((CritterRequest) request);
            case EGG      -> toEggEntity((EggRequest) request);
            case PLANT    -> toPlantEntity((PlantRequest) request);
            case SEED     -> toSeedEntity((SeedRequest) request);
            case FOOD     -> toFoodEntity((FoodRequest) request);
            case PLANET   -> toPlanetEntity((PlanetRequest) request);
            case ARTIFACT -> toArtifactEntity((ArtifactRequest) request);
            case COMET    -> toCometEntity((CometRequest) request);
            case GEYSER   -> toGeyserEntity((GeyserRequest) request);
            case MEDICINE -> toMedicineEntity((MedicineRequest) request);
            case DISEASE  -> toDiseaseEntity((DiseaseRequest) request);
            case PATHOGEN -> toPathogenEntity((PathogenRequest) request);
            case MISC     -> toOtherEntity((OtherRequest) request);
        };
    }

    // ─── Shared helpers ──────────────────────────────────────────────────────

    private ValueUnitDto toValueUnitDto(ValueUnit v) {
        if (v == null) return null;
        return new ValueUnitDto(v.getValue(), v.getUnit());
    }

    private ValueUnit toValueUnit(ValueUnitDto dto) {
        if (dto == null) return null;
        return new ValueUnit(dto.value(), dto.unit());
    }

    private RangeValueDto toRangeValueDto(RangeValue r) {
        if (r == null) return null;
        return new RangeValueDto(r.getMin(), r.getMax(), r.getUnit());
    }

    private RangeValue toRangeValue(RangeValueDto dto) {
        if (dto == null) return null;
        return RangeValue.builder()
                .min(dto.min())
                .max(dto.max())
                .unit(dto.unit())
                .build();
    }

    private RecipeDto toRecipeDto(Recipe r) {
        if (r == null) return null;
        return new RecipeDto(r.getInp(), r.getMiddle(), r.getOut());
    }

    private Recipe toRecipe(RecipeDto dto) {
        if (dto == null) return null;
        return Recipe.builder()
                .inp(dto.in())
                .middle(dto.middle())
                .out(dto.out())
                .build();
    }

    private Map<String, List<RecipeDto>> toMapRecipeDto(Map<String, List<Recipe>> recipes) {
        if (recipes == null) return null;
        Map<String, List<RecipeDto>> dtos = new HashMap<>();
        recipes.forEach((key, list) -> dtos.put(key, list.stream().map(this::toRecipeDto).toList()));
        return dtos;
    }

    private Map<String, List<Recipe>> toMapRecipe(Map<String, List<RecipeDto>> dtos) {
        if (dtos == null) return null;
        Map<String, List<Recipe>> recipes = new HashMap<>();
        dtos.forEach((key, list) -> recipes.put(key, list.stream().map(this::toRecipe).toList()));
        return recipes;
    }

    // ─── Element ─────────────────────────────────────────────────────────────

    private ElementResponse toElementResponse(Element e) {
        return ElementResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .imageUrl(e.getImageUrl())
                .resType(e.getResType())
                .thermalConductivity(e.getThermalConductivity())
                .specificHeatCapacity(e.getSpecificHeatCapacity())
                .molarMass(e.getMolarMass())
                .hardness(e.getHardness())
                .lightAbsorptionFactor(e.getLightAbsorptionFactor())
                .state(e.getState())
                .stateTransition(e.getStateTransition())
                .recipes(toMapRecipeDto(e.getRecipes()))
                .build();
    }

    private Element toElementEntity(ElementRequest r) {
        return Element.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .thermalConductivity(r.getThermalConductivity())
                .specificHeatCapacity(r.getSpecificHeatCapacity())
                .molarMass(r.getMolarMass())
                .hardness(r.getHardness())
                .lightAbsorptionFactor(r.getLightAbsorptionFactor())
                .state(r.getState())
                .stateTransition(r.getStateTransition())
                .recipes(toMapRecipe(r.getRecipes()))
                .build();
    }

    // ─── Building ────────────────────────────────────────────────────────────

    private BuildingResponse toBuildingResponse(Building b) {
        return BuildingResponse.builder()
                .id(b.getId())
                .name(b.getName())
                .description(b.getDescription())
                .imageUrl(b.getImageUrl())
                .resType(b.getResType())
                .size(b.getSize())
                .decorRadius(b.getDecorRadius())
                .decorEffect(b.getDecorEffect())
                .thermalConductivity(b.getThermalConductivity())
                .storageCapacity(toValueUnitDto(b.getStorageCapacity()))
                .power(toValueUnitDto(b.getPower()))
                .heat(toValueUnitDto(b.getHeat()))
                .overheatTemp(toValueUnitDto(b.getOverheatTemp()))
                .recipes(toMapRecipeDto(b.getRecipes()))
                .build();
    }

    private Building toBuildingEntity(BuildingRequest r) {
        return Building.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .size(r.getSize())
                .decorRadius(r.getDecorRadius())
                .decorEffect(r.getDecorEffect())
                .thermalConductivity(r.getThermalConductivity())
                .storageCapacity(toValueUnit(r.getStorageCapacity()))
                .power(toValueUnit(r.getPower()))
                .heat(toValueUnit(r.getHeat()))
                .overheatTemp(toValueUnit(r.getOverheatTemp()))
                .recipes(toMapRecipe(r.getRecipes()))
                .build();
    }

    // ─── Critter ─────────────────────────────────────────────────────────────

    private CritterResponse toCritterResponse(Critter c) {
        return CritterResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .imageUrl(c.getImageUrl())
                .resType(c.getResType())
                .livableRange(toRangeValueDto(c.getLivableRange()))
                .decorRadius(c.getDecorRadius())
                .decorEffect(c.getDecorEffect())
                .caloriesNeeded(toValueUnitDto(c.getCaloriesNeeded()))
                .hp(c.getHp())
                .spaceRequired(c.getSpaceRequired())
                .layEggEvery(toValueUnitDto(c.getLayEggEvery()))
                .dropsOnDeath(c.getDropsOnDeath())
                .isWild(c.isWild())
                .isGlum(c.isGlum())
                .build();
    }

    private Critter toCritterEntity(CritterRequest r) {
        return Critter.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .livableRange(toRangeValue(r.getLivableRange()))
                .decorRadius(r.getDecorRadius())
                .decorEffect(r.getDecorEffect())
                .caloriesNeeded(toValueUnit(r.getCaloriesNeeded()))
                .hp(r.getHp())
                .spaceRequired(r.getSpaceRequired())
                .layEggEvery(toValueUnit(r.getLayEggEvery()))
                .dropsOnDeath(r.getDropsOnDeath())
                .isWild(r.isWild())
                .isGlum(r.isGlum())
                .build();
    }

    // ─── Egg ─────────────────────────────────────────────────────────────────

    private EggResponse toEggResponse(Egg e) {
        return EggResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .imageUrl(e.getImageUrl())
                .resType(e.getResType())
                .laidBy(e.getLaidBy())
                .build();
    }

    private Egg toEggEntity(EggRequest r) {
        return Egg.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .laidBy(r.getLaidBy())
                .build();
    }

    // ─── Plant ───────────────────────────────────────────────────────────────

    private PlantResponse toPlantResponse(Plant p) {
        return PlantResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .imageUrl(p.getImageUrl())
                .resType(p.getResType())
                .airPressure(toRangeValueDto(p.getAirPressure()))
                .temp(toRangeValueDto(p.getTemp()))
                .baseDecor(p.getBaseDecor())
                .build();
    }

    private Plant toPlantEntity(PlantRequest r) {
        return Plant.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .airPressure(toRangeValue(r.getAirPressure()))
                .temp(toRangeValue(r.getTemp()))
                .baseDecor(r.getBaseDecor())
                .build();
    }

    // ─── Seed ────────────────────────────────────────────────────────────────

    private SeedResponse toSeedResponse(Seed s) {
        return SeedResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .imageUrl(s.getImageUrl())
                .resType(s.getResType())
                .test(s.getTest())
                .build();
    }

    private Seed toSeedEntity(SeedRequest r) {
        return Seed.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .test(r.getTest())
                .build();
    }

    // ─── Food ────────────────────────────────────────────────────────────────

    private FoodResponse toFoodResponse(Food f) {
        return FoodResponse.builder()
                .id(f.getId())
                .name(f.getName())
                .description(f.getDescription())
                .imageUrl(f.getImageUrl())
                .resType(f.getResType())
                .quality(f.getQuality())
                .calories(toValueUnitDto(f.getCalories()))
                .spoilTime(toValueUnitDto(f.getSpoilTime()))
                .recipes(toMapRecipeDto(f.getRecipes()))
                .build();
    }

    private Food toFoodEntity(FoodRequest r) {
        return Food.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .quality(r.getQuality())
                .calories(toValueUnit(r.getCalories()))
                .spoilTime(toValueUnit(r.getSpoilTime()))
                .recipes(toMapRecipe(r.getRecipes()))
                .build();
    }

    // ─── Planet ──────────────────────────────────────────────────────────────

    private PlanetResponse toPlanetResponse(Planet p) {
        return PlanetResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .imageUrl(p.getImageUrl())
                .resType(p.getResType())
                .composition(p.getComposition())
                .resources(p.getResources())
                .build();
    }

    private Planet toPlanetEntity(PlanetRequest r) {
        return Planet.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .composition(r.getComposition())
                .resources(r.getResources())
                .build();
    }

    // ─── Artifact ────────────────────────────────────────────────────────────

    private ArtifactResponse toArtifactResponse(Artifact a) {
        return ArtifactResponse.builder()
                .id(a.getId())
                .name(a.getName())
                .description(a.getDescription())
                .imageUrl(a.getImageUrl())
                .resType(a.getResType())
                .tier(a.getTier())
                .decorRadius(a.getDecorRadius())
                .decorEffect(a.getDecorEffect())
                .build();
    }

    private Artifact toArtifactEntity(ArtifactRequest r) {
        return Artifact.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .tier(r.getTier())
                .decorRadius(r.getDecorRadius())
                .decorEffect(r.getDecorEffect())
                .build();
    }

    // ─── Comet ───────────────────────────────────────────────────────────────

    private CometResponse toCometResponse(Comet c) {
        return CometResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .imageUrl(c.getImageUrl())
                .resType(c.getResType())
                .drops(c.getDrops())
                .composition(toRangeValueDto(c.getComposition()))
                .temperature(c.getTemperature())
                .build();
    }

    private Comet toCometEntity(CometRequest r) {
        return Comet.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .drops(r.getDrops())
                .composition(toRangeValue(r.getComposition()))
                .temperature(r.getTemperature())
                .build();
    }

    // ─── Geyser ──────────────────────────────────────────────────────────────

    private GeyserResponse toGeyserResponse(Geyser g) {
        return GeyserResponse.builder()
                .id(g.getId())
                .name(g.getName())
                .description(g.getDescription())
                .imageUrl(g.getImageUrl())
                .resType(g.getResType())
                .maxPressure(toValueUnitDto(g.getMaxPressure()))
                .avgEmissionRate(toRangeValueDto(g.getAvgEmissionRate()))
                .totalEruptionPeriod(toRangeValueDto(g.getTotalEruptionPeriod()))
                .activeOfEruptionPeriod(toRangeValueDto(g.getActiveOfEruptionPeriod()))
                .totalPeriod(toRangeValueDto(g.getTotalPeriod()))
                .activePeriod(toRangeValueDto(g.getActivePeriod()))
                .build();
    }

    private Geyser toGeyserEntity(GeyserRequest r) {
        return Geyser.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .maxPressure(toValueUnit(r.getMaxPressure()))
                .avgEmissionRate(toRangeValue(r.getAvgEmissionRate()))
                .totalEruptionPeriod(toRangeValue(r.getTotalEruptionPeriod()))
                .activeOfEruptionPeriod(toRangeValue(r.getActiveOfEruptionPeriod()))
                .totalPeriod(toRangeValue(r.getTotalPeriod()))
                .activePeriod(toRangeValue(r.getActivePeriod()))
                .build();
    }

    // ─── Medicine ────────────────────────────────────────────────────────────

    private MedicineResponse toMedicineResponse(Medicine m) {
        return MedicineResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .description(m.getDescription())
                .imageUrl(m.getImageUrl())
                .resType(m.getResType())
                .duration(toValueUnitDto(m.getDuration()))
                .radiationRecovery(m.getRadiationRecovery())
                .germResistance(m.getGermResistance())
                .disease(m.getDisease())
                .recipe(toRecipeDto(m.getRecipe()))
                .build();
    }

    private Medicine toMedicineEntity(MedicineRequest r) {
        return Medicine.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .duration(toValueUnit(r.getDuration()))
                .radiationRecovery(r.getRadiationRecovery())
                .germResistance(r.getGermResistance())
                .disease(r.getDisease())
                .recipe(toRecipe(r.getRecipe()))
                .build();
    }

    // ─── Disease ─────────────────────────────────────────────────────────────

    private DiseaseResponse toDiseaseResponse(Disease d) {
        return DiseaseResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .imageUrl(d.getImageUrl())
                .resType(d.getResType())
                .duration(toValueUnitDto(d.getDuration()))
                .severity(d.getSeverity())
                .type(d.getType())
                .gotBy(d.getGotBy())
                .curedBy(d.getCuredBy())
                .causedBy(d.getCausedBy())
                .build();
    }

    private Disease toDiseaseEntity(DiseaseRequest r) {
        return Disease.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .duration(toValueUnit(r.getDuration()))
                .severity(r.getSeverity())
                .type(r.getType())
                .gotBy(r.getGotBy())
                .curedBy(r.getCuredBy())
                .causedBy(r.getCausedBy())
                .build();
    }

    // ─── Pathogen ────────────────────────────────────────────────────────────

    private PathogenResponse toPathogenResponse(Pathogen p) {
        return PathogenResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .imageUrl(p.getImageUrl())
                .resType(p.getResType())
                .temperature(toRangeValueDto(p.getTemperature()))
                .canCause(p.getCanCause())
                .droppedBy(p.getDroppedBy())
                .disinfectedBy(p.getDisinfectedBy())
                .build();
    }

    private Pathogen toPathogenEntity(PathogenRequest r) {
        return Pathogen.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .temperature(toRangeValue(r.getTemperature()))
                .canCause(r.getCanCause())
                .droppedBy(r.getDroppedBy())
                .disinfectedBy(r.getDisinfectedBy())
                .build();
    }

    // ─── Other ───────────────────────────────────────────────────────────────

    private OtherResponse toOtherResponse(Other o) {
        return OtherResponse.builder()
                .id(o.getId())
                .name(o.getName())
                .description(o.getDescription())
                .imageUrl(o.getImageUrl())
                .resType(o.getResType())
                .test(o.getTest())
                .build();
    }

    private Other toOtherEntity(OtherRequest r) {
        return Other.builder()
                .name(r.getName())
                .description(r.getDescription())
                .imageUrl(r.getImageUrl())
                .resType(r.getResType())
                .test(r.getTest())
                .build();
    }}
