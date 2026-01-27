package com.rawsaurus.sleep_not_included.build.service;

import com.rawsaurus.sleep_not_included.build.dto.BuildRequest;
import com.rawsaurus.sleep_not_included.build.dto.BuildResponse;
import com.rawsaurus.sleep_not_included.build.mapper.BuildMapper;
import com.rawsaurus.sleep_not_included.build.model.Build;
import com.rawsaurus.sleep_not_included.build.model.LikedBuilds;
import com.rawsaurus.sleep_not_included.build.repo.BuildRepository;
import com.rawsaurus.sleep_not_included.build.repo.LikedBuildsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BuildService {

    private final BuildRepository buildRepo;
    private final LikedBuildsRepository likedBuildsRepo;

    private final BuildMapper buildMapper;

    public BuildResponse findById(Long id){
        return buildMapper.toResponse(
                buildRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Build not found"))
        );
    }

    public BuildResponse findByName(String name){
        return buildMapper.toResponse(
                buildRepo.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Build not found"))
        );
    }

    public Page<BuildResponse> suggestSearch(String name){
        Pageable pageable = PageRequest.of(0, 5);
        return buildRepo.findAllByNameLikeIgnoreCase(name, pageable)
                .map(buildMapper::toResponse);
    }

    public Page<BuildResponse> findAll(Pageable pageable){
        return buildRepo.findAll(pageable)
                .map(buildMapper::toResponse);
    }

    public Page<BuildResponse> findAllWithFilters(Set<Long> tags, Set<Long> dlc, Pageable pageable){
        return buildRepo.findAllByDlcIdAndTagsId(dlc, tags, pageable)
                .map(buildMapper::toResponse);
    }

    public Page<BuildResponse> findAllFromUser(Long userId, Pageable pageable){
        return buildRepo.findAllByCreatorId(userId, pageable)
                .map(buildMapper::toResponse);
    }

    public List<BuildResponse> findAllByLikes(Long userId){
        //check user
        List<LikedBuilds> likedBuilds = likedBuildsRepo.findAllByUserId(userId);
        List<BuildResponse> builds = new ArrayList<>();
        for(LikedBuilds l : likedBuilds){
            builds.add(
                    buildMapper.toResponse(
                            buildRepo.findById(l.getBuildId())
                                    .orElseThrow(() -> new EntityNotFoundException("Build not found"))
                    )
            );
        }
        return builds;
    }

    public BuildResponse createBuild(Long creatorId, BuildRequest request){
        Build build = buildMapper.toEntity(request);
        build.setCreatorId(creatorId);
        return buildMapper.toResponse(buildRepo.save(build));
    }

    public void likeBuild(Long userId, Long buildId){
        //check user
        Build build = buildRepo.findById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));

        Optional<LikedBuilds> likedBuilds = likedBuildsRepo.findByUserIdAndBuildId(userId, buildId);

        if(likedBuilds.isPresent()){
            build.setLikes(build.getLikes() - 1);
            likedBuildsRepo.delete(likedBuilds.get());
        }else {
            build.setLikes(build.getLikes() + 1);
            LikedBuilds likedBuildsToSave = LikedBuilds.builder()
                    .userId(userId)
                    .buildId(buildId)
                    .build();
            likedBuildsRepo.save(likedBuildsToSave);
        }
        buildRepo.save(build);
    }

    public BuildResponse updateBuild(Long userId, Long buildId, BuildRequest request){
        //check user
        Build build = buildRepo.findById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));

        buildMapper.updateToEntity(request, build);

        return buildMapper.toResponse(buildRepo.save(build));
    }

    public String deleteBuild(Long userId, Long buildId){
        //check user
        Build build = buildRepo.findById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));
        List<LikedBuilds> likedBuilds = likedBuildsRepo.findAllByBuildId(buildId);

        buildRepo.delete(build);
        likedBuildsRepo.deleteAll(likedBuilds);

        return "Build deleted successfully";
    }

    public void deleteAllFromUser(Long userId){
        List<Build> builds = buildRepo.findAllByCreatorId(userId);
        List<LikedBuilds> likedBuilds = likedBuildsRepo.findAllByUserId(userId);

        buildRepo.deleteAll(builds);
        likedBuildsRepo.deleteAll(likedBuilds);
    }
}
