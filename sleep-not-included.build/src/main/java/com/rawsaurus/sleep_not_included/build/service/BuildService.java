package com.rawsaurus.sleep_not_included.build.service;

import com.rawsaurus.sleep_not_included.build.clients.TagClient;
import com.rawsaurus.sleep_not_included.build.clients.UserClient;
import com.rawsaurus.sleep_not_included.build.config.RabbitMQConfig;
import com.rawsaurus.sleep_not_included.build.dto.*;
import com.rawsaurus.sleep_not_included.build.mapper.BuildMapper;
import com.rawsaurus.sleep_not_included.build.model.Build;
import com.rawsaurus.sleep_not_included.build.model.LikedBuilds;
import com.rawsaurus.sleep_not_included.build.repo.BuildRepository;
import com.rawsaurus.sleep_not_included.build.repo.LikedBuildsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BuildService {

    private final BuildRepository buildRepo;
    private final LikedBuildsRepository likedBuildsRepo;

    private final UserClient userClient;
    private final TagClient tagClient;

    private final BuildMapper buildMapper;

    public String test(Long userId){
        return userClient.findUserById(userId).getBody().toString();
    }

    public BuildResponse findById(Long id){
//        Build build = buildRepo.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Build not found"));
//        List<TagResponse> tags = tagClient.findAllByBuild(id).getBody();
//
//        return new BuildResponse(
//                build.getId(),
//                build.getName(),
//                build.getDescription(),
//                tags,
//                build.getCreatorId(),
//                build.getLikes()
//        );

        return buildMapper.toResponse(
                buildRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Build not found"))
        );
    }

    public BuildResponse findByName(String name){
//        Build build = buildRepo.findByName(name)
//                .orElseThrow(() -> new EntityNotFoundException("Build not found"));
//        List<TagResponse> tags = tagClient.findAllByBuild(build.getId()).getBody();
//
//        return new BuildResponse(
//                build.getId(),
//                build.getName(),
//                build.getDescription(),
//                tags,
//                build.getCreatorId(),
//                build.getLikes()
//        );
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

    public Page<BuildResLoggedIn> findAllLoggedIn(Long userId, Pageable pageable){
        var user = userClient.findUserById(userId).getBody();
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }

        Page<Build> builds = buildRepo.findAll(pageable);
        List<BuildResLoggedIn> res = new ArrayList<>();

        for(Build b : builds.getContent()){
            res.add(
                    new BuildResLoggedIn(
                            b.getId(),
                            b.getName(),
                            b.getDescription(),
                            Collections.emptyList(),
                            b.getCreatorId(),
                            b.getLikes(),
                            likedBuildsRepo.existsByUserIdAndBuildId(userId, b.getId())
                    )
            );
        }
        return new PageImpl<>(res);
    }

    //rework
//    public Page<BuildResponse> findAllWithFilters(Set<Long> tags, Set<Long> dlc, Pageable pageable){
//        return buildRepo.findAllByDlcIdAndTagsId(dlc, tags, pageable)
//                .map(buildMapper::toResponse);
//    }

    public Page<BuildResponse> findAllFromUser(Long userId, Pageable pageable){
        var user = userClient.findUserById(userId).getBody();
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
        return buildRepo.findAllByCreatorId(user.id(), pageable)
                .map(buildMapper::toResponse);
    }

    public Page<BuildResponse> findAllLikedBuilds(Long userId, Pageable pageable){
        var user = userClient.findUserById(userId).getBody();
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
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

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), builds.size());

        if (start >= builds.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, builds.size());
        }

        return new PageImpl<BuildResponse>(
                builds.subList(start, end), pageable, builds.size()
        );
    }

    public BuildResponse createBuild(Long creatorId, BuildRequest request){
        var user = userClient.findUserById(creatorId).getBody();
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
        Build build = buildMapper.toEntity(request);
        build.setCreatorId(user.id());
        var res = buildMapper.toResponse(buildRepo.save(build));
        if(!request.tagId().isEmpty()) {
            tagClient.addTagsToBuild(res.id(), request.tagId());
        }

        return res;
    }

    public void likeBuild(Long userId, Long buildId){
        var user = userClient.findUserById(userId).getBody();
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
        Build build = buildRepo.findById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));

        Optional<LikedBuilds> likedBuilds = likedBuildsRepo.findByUserIdAndBuildId(user.id(), buildId);

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
        var user = userClient.findUserById(userId).getBody();
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
        Build build = buildRepo.findById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));

        buildMapper.updateToEntity(request, build);

        return buildMapper.toResponse(buildRepo.save(build));
    }

    @Transient
    public String deleteBuild(Long userId, Long buildId){
        var user = userClient.findUserById(userId).getBody();
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
        Build build = buildRepo.findById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));
        List<LikedBuilds> likedBuilds = likedBuildsRepo.findAllByBuildId(buildId);

        buildRepo.delete(build);
        likedBuildsRepo.deleteAll(likedBuilds);

        return "Build deleted successfully";
    }

    @Transient
    public void deleteAllFromUser(Long userId){
        var user = userClient.findUserById(userId).getBody();
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
        List<Build> builds = buildRepo.findAllByCreatorId(user.id());
        List<LikedBuilds> likedBuilds = likedBuildsRepo.findAllByUserId(user.id());

        buildRepo.deleteAll(builds);
        likedBuildsRepo.deleteAll(likedBuilds);
    }

    @RabbitListener(queues = RabbitMQConfig.queueName)
    @Transactional
    public void deleteBuildsFromUser(DeleteEntityEvent event){
        List<Build> builds = buildRepo.findAllByCreatorId(event.id());
        List<LikedBuilds> likedBuilds = likedBuildsRepo.findAllByUserId(event.id());

        buildRepo.deleteAll(builds);
        likedBuildsRepo.deleteAll(likedBuilds);

        System.out.println("Builds deleted");
    }
}
