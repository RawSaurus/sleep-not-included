package com.rawsaurus.sleep_not_included.build.service;

import com.rawsaurus.sleep_not_included.build.clients.ImageClient;
import com.rawsaurus.sleep_not_included.build.clients.TagClient;
import com.rawsaurus.sleep_not_included.build.clients.UserClient;
import com.rawsaurus.sleep_not_included.build.config.RabbitMQConfig;
import com.rawsaurus.sleep_not_included.build.dto.*;
import com.rawsaurus.sleep_not_included.build.handler.ActionNotAllowed;
import com.rawsaurus.sleep_not_included.build.mapper.BuildMapper;
import com.rawsaurus.sleep_not_included.build.model.Build;
import com.rawsaurus.sleep_not_included.build.model.BuildTags;
import com.rawsaurus.sleep_not_included.build.model.LikedBuilds;
import com.rawsaurus.sleep_not_included.build.repo.BuildRepository;
import com.rawsaurus.sleep_not_included.build.repo.BuildTagsRepository;
import com.rawsaurus.sleep_not_included.build.repo.LikedBuildsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.rawsaurus.sleep_not_included.build.config.RabbitMQConfig.*;

@Service
@RequiredArgsConstructor
public class BuildService {

    private final BuildRepository buildRepo;
    private final LikedBuildsRepository likedBuildsRepo;
    private final BuildTagsRepository buildTagsRepo;

    private final UserClient userClient;
    private final TagClient tagClient;
    private final ImageClient imageClient;

    private final BuildMapper buildMapper;
    private final RabbitTemplate rabbitTemplate;

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

//    public BuildResponse findByName(String name){
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
//        return buildMapper.toResponse(
//                buildRepo.findByName(name)
//                        .orElseThrow(() -> new EntityNotFoundException("Build not found"))
//        );
//    }

    public BuildDetailResponse findByName(String name){
        Build build = buildRepo.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));
        String creatorUsername = userClient.findUserById(build.getCreatorId()).getBody().username();
        List<ImageResponse> imageUrls = imageClient.findByOwner("build", build.getId()).getBody();
        List<TagResponse> tags = tagClient.findAllByIds(
                buildTagsRepo.findAllByBuildId(build.getId())
                        .stream().map(BuildTags::getTagId).toList()
        ).getBody();

        return toBuildDetailResponse(build, creatorUsername, imageUrls, tags);
    }

    public BuildDetailResponse findBuildDetailsById(Long id){
        Build build = buildRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));
        String creatorUsername = userClient.findUserById(build.getCreatorId()).getBody().username();
        List<ImageResponse> imageUrls = imageClient.findByOwner("build", build.getId()).getBody();
        List<TagResponse> tags = tagClient.findAllByIds(
                buildTagsRepo.findAllByBuildId(build.getId())
                        .stream().map(BuildTags::getTagId).toList()
        ).getBody();

        return toBuildDetailResponse(build, creatorUsername, imageUrls, tags);
    }

    public Page<BuildDetailResponse> findAllBuildDetails(Pageable pageable) {
        Page<Build> builds = buildRepo.findAll(pageable);
        List<Build> content = builds.getContent();

        if (content.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> buildIds = extractBuildIds(content);
        List<Long> creatorIds = extractCreatorIds(content);

        Map<Long, String> usernameByCreatorId = fetchUsernamesByIds(creatorIds);
        Map<Long, List<ImageResponse>> imagesByBuildId = fetchImagesByBuildIds(buildIds);
        Map<Long, List<TagResponse>> tagsByBuildId = fetchTagsByBuildIds(buildIds);

        List<BuildDetailResponse> responses = content.stream()
                .map(build -> toBuildDetailResponse(build, usernameByCreatorId, imagesByBuildId, tagsByBuildId))
                .toList();

        return new PageImpl<>(responses, pageable, builds.getTotalElements());
    }

    public Page<BuildDetailResponse> findAllBuildDetailsByName(String name, Pageable pageable) {
        List<Build> content = buildRepo.searchBuilds(name, pageable);
//        List<Build> content = builds.getContent();

        if (content.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> buildIds = extractBuildIds(content);
        List<Long> creatorIds = extractCreatorIds(content);

        Map<Long, String> usernameByCreatorId = fetchUsernamesByIds(creatorIds);
        Map<Long, List<ImageResponse>> imagesByBuildId = fetchImagesByBuildIds(buildIds);
        Map<Long, List<TagResponse>> tagsByBuildId = fetchTagsByBuildIds(buildIds);

        List<BuildDetailResponse> responses = content.stream()
                .map(build -> toBuildDetailResponse(build, usernameByCreatorId, imagesByBuildId, tagsByBuildId))
                .toList();

        return new PageImpl<>(responses, pageable, content.size());
    }

    public Page<BuildDetailResponse> findAllBuildDetailsByTags(List<TagResponse> tags, Pageable pageable) {
        List<Long> tagIds = tags.stream().map(TagResponse::id).toList();
        Page<BuildTags> buildTags = buildTagsRepo.findAllByTagIdIn(tagIds, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        Page<Build> builds = buildRepo.findAllByIdIn(
                buildTags.getContent().stream().map(BuildTags::getBuildId).toList(),
                pageable
        );
        List<Build> content = builds.getContent();

        if (content.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> buildIds = extractBuildIds(content);
        List<Long> creatorIds = extractCreatorIds(content);

        Map<Long, String> usernameByCreatorId = fetchUsernamesByIds(creatorIds);
        Map<Long, List<ImageResponse>> imagesByBuildId = fetchImagesByBuildIds(buildIds);
        Map<Long, List<TagResponse>> tagsByBuildId = fetchTagsByBuildIds(buildIds);

        List<BuildDetailResponse> responses = content.stream()
                .map(build -> toBuildDetailResponse(build, usernameByCreatorId, imagesByBuildId, tagsByBuildId))
                .toList();

        return new PageImpl<>(responses, pageable, content.size());
    }

    public List<BuildResponse> suggestSearch(String name){
        Pageable pageable = PageRequest.of(0, 5);
        return buildRepo.searchBuilds(name, pageable)
                .stream()
                .map(buildMapper::toResponse)
                .toList();
    }

        //would be too bad on performance, solve by join on tables
//    public Page<BuildDetailResponse> findAllWithFilters(String name, List<TagResponse> tags, Pageable pageable){
//        //nothing
//        if((name == null || name.isEmpty()) && (tags == null || tags.isEmpty())){
//            return findAllBuildDetails(pageable);
//        }
//        //name
//        if(tags == null || tags.isEmpty()) {
//            return findAllBuildDetailsByName(name, pageable);
//        }
//        //tag
//        if(name == null || name.isEmpty()) {
//            return findAllBuildDetailsByTags(tags, pageable);
//        }
//        //name && tags
//        return null;
//    }

//    public Page<BuildResponse> findAll(Pageable pageable){
//        return buildRepo.findAll(pageable)
//                .map(buildMapper::toResponse);
//    }

//    public Page<BuildResLoggedIn> findAllLoggedIn(Long userId, Pageable pageable){ var user = userClient.findUserById(userId).getBody();
//        if (user == null){
//            throw new EntityNotFoundException("User not found");
//        }
//
//        Page<Build> builds = buildRepo.findAll(pageable);
//        List<BuildResLoggedIn> res = new ArrayList<>();
//
//        for(Build b : builds.getContent()){
//            res.add(
//                    new BuildResLoggedIn(
//                            b.getId(),
//                            b.getName(),
//                            b.getDescription(),
//                            b.getCreatorId(),
//                            b.getLikes(),
//                            likedBuildsRepo.existsByUserIdAndBuildId(userId, b.getId())
//                    )
//            );
//        }
//        return new PageImpl<>(res);
//    }

//    public List<BuildTags> findAllBuildsByTags(List<Tag> tags){
//        return null;
//    }
//
//    public List<TagResponse> findAll(){
//        return tagRepo.findAll()
//                .stream()
//                .map(tagMapper::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    public List<TagResponse> findAllByBuild(Long buildId){
//        //check build
//        List<BuildTags> tagIds = buildTagsRepo.findAllByBuildId(buildId);
//        List<TagResponse> tags = new ArrayList<>();
//
//        for(BuildTags b : tagIds){
//            tags.add(
//                    tagMapper.toResponse(
//                            tagRepo.findById(b.getTagId())
//                                    .orElseThrow(() -> new EntityNotFoundException("Tag not found"))
//                    )
//            );
//        }
//        return tags;
//    }
    //rework
//    public Page<BuildResponse> findAllWithFilters(Set<Long> tags, Set<Long> dlc, Pageable pageable){
//        return buildRepo.findAllByDlcIdAndTagsId(dlc, tags, pageable)
//                .map(buildMapper::toResponse);
//    }

    public List<TagResponse> findAllTagsByBuild(Long buildId){
        List<BuildTags> buildTags = buildTagsRepo.findAllByBuildId(buildId);
        List<Long> tagIds = new ArrayList<>();

        for(BuildTags b : buildTags){
            tagIds.add(b.getTagId());
        }
        return tagClient.findAllByIds(tagIds).getBody();
    }

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
            addTagsToBuild(res.id(), request.tagId());
        }
        return res;
    }

    public void addTagsToBuild(Long buildId, List<Long> tagIds){
        //check tags?
        for(Long id : tagIds){
            BuildTags b = BuildTags.builder()
                    .buildId(buildId)
                    .tagId(id)
                    .build();
            buildTagsRepo.save(b);
        }
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

    @Transactional
    public BuildResponse updateBuild(Long userId, Long buildId, BuildRequest request){
        var user = userClient.findUserById(userId).getBody();
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
        Build build = buildRepo.findById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));

        if(!user.id().equals(build.getCreatorId())){
            throw new ActionNotAllowed("You cannot update this build");
        }

        buildMapper.updateToEntity(request, build);

        //try come up with better impl
        if(!request.tagId().isEmpty()) {
            buildTagsRepo.deleteAllByBuildId(build.getId());
            addTagsToBuild(build.getId(), request.tagId());
        }

        return buildMapper.toResponse(buildRepo.save(build));
    }

    @Transactional
    public String deleteBuild(Long userId, Long buildId){
        var user = userClient.findUserById(userId);
        if (user.getBody() == null){
            throw new EntityNotFoundException("User not found");
        }
        Build build = buildRepo.findById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));

        if(!user.getBody().id().equals(build.getCreatorId())){
            throw new ActionNotAllowed("You cannot delete this build");
        }

        List<LikedBuilds> likedBuilds = likedBuildsRepo.findAllByBuildId(buildId);

        buildRepo.delete(build);
        likedBuildsRepo.deleteAll(likedBuilds);
        buildTagsRepo.deleteAllByBuildId(build.getId());

        rabbitTemplate.convertAndSend(
                BUILD_EVENT_EXCHANGE,
                "",
                new DeleteEntityEvent("build", buildId)
        );

        return "Build deleted successfully";
    }

    //not needed
    @Transactional
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

    @RabbitListener(queues = BUILD_USER_DELETED_QUEUE)
    @Transactional
    public void deleteBuildsFromUser(DeleteEntityEvent event){
        List<Build> builds = buildRepo.findAllByCreatorId(event.id());
        List<LikedBuilds> likedBuilds = likedBuildsRepo.findAllByUserId(event.id());

        buildRepo.deleteAll(builds);
        likedBuildsRepo.deleteAll(likedBuilds);
        for(Build b : builds){
            buildTagsRepo.deleteAllByBuildId(b.getId());

            rabbitTemplate.convertAndSend(
                    BUILD_EVENT_EXCHANGE,
                    "",
                    new DeleteEntityEvent("build", b.getId())
            );
        }
        System.out.println("Builds deleted");
    }

    @RabbitListener(queues = BUILD_TAG_DELETED_QUEUE)
    @Transactional
    public void deleteTagsFromBuild(DeleteEntityEvent event){
        buildTagsRepo.deleteAllByTagId(event.id());
    }

    private List<Long> extractBuildIds(List<Build> builds) {
        return builds.stream().map(Build::getId).toList();
    }

    private List<Long> extractCreatorIds(List<Build> builds) {
        return builds.stream().map(Build::getCreatorId).toList();
    }

    private Map<Long, String> fetchUsernamesByIds(List<Long> creatorIds) {
        List<UserResponse> users = userClient.findAllByIds(creatorIds).getBody();
        if (users == null) return Map.of();
        return users.stream().collect(Collectors.toMap(UserResponse::id, UserResponse::username));
    }

    private Map<Long, List<ImageResponse>> fetchImagesByBuildIds(List<Long> buildIds) {
        List<ImageResponse> allImages = imageClient.findAllByOwnerIds("build", buildIds).getBody();
        if (allImages == null) return Map.of();
        return allImages.stream().collect(Collectors.groupingBy(ImageResponse::ownerId));
    }

    private Map<Long, List<TagResponse>> fetchTagsByBuildIds(List<Long> buildIds) {
        List<BuildTags> allBuildTags = buildTagsRepo.findAllByBuildIdIn(buildIds);

        List<Long> tagIds = allBuildTags.stream().map(BuildTags::getTagId).distinct().toList();
        if (tagIds.isEmpty()) return Map.of();

        List<TagResponse> allTags = tagClient.findAllByIds(tagIds).getBody();
        if (allTags == null) return Map.of();

        Map<Long, TagResponse> tagById = allTags.stream()
                .collect(Collectors.toMap(TagResponse::id, t -> t));

        return allBuildTags.stream().collect(Collectors.groupingBy(
                BuildTags::getBuildId,
                Collectors.mapping(bt -> tagById.get(bt.getTagId()), Collectors.toList())
        ));
    }

    private BuildDetailResponse toBuildDetailResponse(
            Build build,
            String username,
            List<ImageResponse> imagesRes,
            List<TagResponse> tagsRes
    ) {
//        List<ImageResponse> images = imagesByBuildId.getOrDefault(build.getId(), List.of());
        String thumbnailUrl = "";
        List<String> otherImageUrls = new ArrayList<>();

        for (ImageResponse image : imagesRes) {
            if ("BUILD_THUMBNAIL".equals(image.type()) && thumbnailUrl.isEmpty()) {
                thumbnailUrl = image.url();
            } else {
                otherImageUrls.add(image.url());
            }
        }

        Long userId = resolveUserId();
        Boolean isLiked = userId != null
                ? likedBuildsRepo.existsByUserIdAndBuildId(userId, build.getId())
                : null;

        return new BuildDetailResponse(
                build.getId(),
                build.getName(),
                build.getDescription(),
                build.getLikes(),
                build.getCreatedAt(),
                username,
                thumbnailUrl,
                otherImageUrls,
                tagsRes,
                isLiked
        );
    }

    private BuildDetailResponse toBuildDetailResponse(
            Build build,
            Map<Long, String> usernameByCreatorId,
            Map<Long, List<ImageResponse>> imagesByBuildId,
            Map<Long, List<TagResponse>> tagsByBuildId
    ) {
        List<ImageResponse> images = imagesByBuildId.getOrDefault(build.getId(), List.of());
        String thumbnailUrl = "";
        List<String> otherImageUrls = new ArrayList<>();

        for (ImageResponse image : images) {
            if ("BUILD_THUMBNAIL".equals(image.type()) && thumbnailUrl.isEmpty()) {
                thumbnailUrl = image.url();
            } else {
                otherImageUrls.add(image.url());
            }
        }

        Long userId = resolveUserId();
        Boolean isLiked = userId != null
                ? likedBuildsRepo.existsByUserIdAndBuildId(userId, build.getId())
                : null;

        return new BuildDetailResponse(
                build.getId(),
                build.getName(),
                build.getDescription(),
                build.getLikes(),
                build.getCreatedAt(),
                usernameByCreatorId.getOrDefault(build.getCreatorId(), ""),
                thumbnailUrl,
                otherImageUrls,
                tagsByBuildId.getOrDefault(build.getId(), List.of()),
                isLiked
        );
    }

    private Long resolveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getSubject(); // "sub" claim
        var user = userClient.findUserByKeycloakId(keycloakId).getBody();
        return user != null ? user.id() : null;
    }
}