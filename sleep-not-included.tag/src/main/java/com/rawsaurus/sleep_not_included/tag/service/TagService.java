package com.rawsaurus.sleep_not_included.tag.service;

import com.rawsaurus.sleep_not_included.tag.config.RabbitMQConfig;
import com.rawsaurus.sleep_not_included.tag.dto.DeleteEntityEvent;
import com.rawsaurus.sleep_not_included.tag.dto.TagRequest;
import com.rawsaurus.sleep_not_included.tag.dto.TagResponse;
import com.rawsaurus.sleep_not_included.tag.mapper.TagMapper;
import com.rawsaurus.sleep_not_included.tag.model.Tag;
import com.rawsaurus.sleep_not_included.tag.model.Type;
import com.rawsaurus.sleep_not_included.tag.repo.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepo;

    private final TagMapper tagMapper;

    private final RabbitTemplate rabbitTemplate;

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

    public List<TagResponse> findAllByIds(List<Long> ids) {
        return tagRepo.findAllByIdIn(ids)
                .stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    public Page<TagResponse> findAll(Pageable pageable){
        return tagRepo.findAll(pageable)
                .map(tagMapper::toResponse);
    }

//    public List<TagResponse> findAllByIds(List<Long> ids){
//        return tagRepo.findAllById(ids)
//                .stream()
//                .map(tagMapper::toResponse)
//                .toList();
//    }

    public TagResponse createTag(TagRequest request){
        return tagMapper.toResponse(
                tagRepo.save(
                        tagMapper.toEntity(request)
                )
        );
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

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TAG_EVENTS_EXCHANGE,
                "",
                new DeleteEntityEvent("tag", tag.getId())
        );

        return "Tag deleted successfully";
    }
}
