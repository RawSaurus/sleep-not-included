package com.rawsaurus.sleep_not_included.tag.service;

import com.rawsaurus.sleep_not_included.tag.dto.TagRequest;
import com.rawsaurus.sleep_not_included.tag.dto.TagResponse;
import com.rawsaurus.sleep_not_included.tag.mapper.TagMapper;
import com.rawsaurus.sleep_not_included.tag.model.BuildTags;
import com.rawsaurus.sleep_not_included.tag.model.Tag;
import com.rawsaurus.sleep_not_included.tag.model.Type;
import com.rawsaurus.sleep_not_included.tag.repo.BuildTagsRepository;
import com.rawsaurus.sleep_not_included.tag.repo.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepo;
    private final BuildTagsRepository buildTagsRepo;

    private final TagMapper tagMapper;

    public TagResponse findById(Long id){
        return tagMapper.toResponse(
                tagRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Tag not found"))
        );
    }

    public TagResponse findByName(String name){
        return tagMapper.toResponse(
                tagRepo.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Tag not found"))
        );
    }

    public List<TagResponse> findAllByType(Type type){
        return tagRepo.findAllByType(type)
                .stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    public List<BuildTags> findAllBuildsByTags(List<Tag> tags){
        return null;
    }

    public List<TagResponse> findAll(){
        return tagRepo.findAll()
                .stream()
                .map(tagMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TagResponse> findAllByBuild(Long buildId){
        //check build
        List<BuildTags> tagIds = buildTagsRepo.findAllByBuildId(buildId);
        List<TagResponse> tags = new ArrayList<>();

        for(BuildTags b : tagIds){
            tags.add(
                    tagMapper.toResponse(
                            tagRepo.findById(b.getTagId())
                                    .orElseThrow(() -> new EntityNotFoundException("Tag not found"))
                    )
            );
        }
        return tags;
    }

    public TagResponse createTag(TagRequest request){
        return tagMapper.toResponse(
                tagRepo.save(
                        tagMapper.toEntity(request)
                )
        );
    }

    public void addTagsToBuild(Long buildId, List<Long> tagIds){
        //check build and tags?
        for(Long id : tagIds){
            BuildTags b = BuildTags.builder()
                    .buildId(buildId)
                    .tagId(id)
                    .build();
            buildTagsRepo.save(b);
        }
    }

    public TagResponse updateTag(Long id, TagRequest request){
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));

        tagMapper.updateToEntity(request, tag);

        return tagMapper.toResponse(
                tagRepo.save(tag)
        );
    }

    public String deleteTag(Long id){
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));

        tagRepo.delete(tag);

        return "Tag deleted successfully";
    }
}
