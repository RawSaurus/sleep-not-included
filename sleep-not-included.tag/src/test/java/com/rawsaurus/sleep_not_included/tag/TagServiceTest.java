package com.rawsaurus.sleep_not_included.tag;

import com.rawsaurus.sleep_not_included.tag.config.RabbitMQConfig;
import com.rawsaurus.sleep_not_included.tag.dto.DeleteEntityEvent;
import com.rawsaurus.sleep_not_included.tag.dto.TagRequest;
import com.rawsaurus.sleep_not_included.tag.dto.TagResponse;
import com.rawsaurus.sleep_not_included.tag.mapper.TagMapper;
import com.rawsaurus.sleep_not_included.tag.mapper.TagMapperImpl;
import com.rawsaurus.sleep_not_included.tag.model.Tag;
import com.rawsaurus.sleep_not_included.tag.model.Type;
import com.rawsaurus.sleep_not_included.tag.repo.TagRepository;
import com.rawsaurus.sleep_not_included.tag.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.rawsaurus.sleep_not_included.tag.TagTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepo;
    @Mock
    private TagMapper tagMapper;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TagService tagService;


    @Nested
    @DisplayName("findById")
    class findById {

        @Test
        @DisplayName("findById returns mapped response when tag exists")
        void findByIdReturnsMappedResponseWhenTagExists() {
            Tag tag = Tag.builder()
                    .id(1L)
                    .name("Oxygen")
                    .type(Type.BUILD)
                    .build();
            TagResponse res = new TagResponse(1L, "Oxygen", Type.BUILD);

            when(tagRepo.findById(1L)).thenReturn(Optional.of(tag));
            when(tagMapper.toResponse(tag)).thenReturn(res);

            TagResponse result = tagService.findById(1L);

            assertThat(res).isEqualTo(result);
            verify(tagRepo).findById(1L);
            verify(tagMapper).toResponse(tag);
        }

        @Test
        @DisplayName("findById throws when missing")
        void findByIdThrowsWhenMissing() {
            when(tagRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tagService.findById(1L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Tag not found");

            verifyNoInteractions(tagMapper);
        }
    }

    @Nested
    @DisplayName("findByName")
    class findByName {
        @Test
        @DisplayName("findByName returns mapped response when tag exists")
        void findByNameReturnsMappedResponseWhenTagExists() {
            Tag tag = Tag.builder()
                    .id(1L)
                    .name("Oxygen")
                    .type(Type.BUILD)
                    .build();
            TagResponse res = new TagResponse(1L, "Oxygen", Type.BUILD);

            when(tagRepo.findByName("Oxygen")).thenReturn(Optional.of(tag));
            when(tagMapper.toResponse(tag)).thenReturn(res);

            TagResponse result = tagService.findByName("Oxygen");

            assertThat(res).isEqualTo(result);
        }
    }

    @Nested
    @DisplayName("findAllByType")
    class findAllByType {
        @Test
        @DisplayName("findAllByType returns all with type when exists")
        void findAllByTypeReturnsAllWithTypeWhenExists() {
            Tag tag1 = Tag.builder()
                    .id(1L)
                    .name("Oxygen1")
                    .type(Type.BUILD)
                    .build();
            TagResponse res1 = new TagResponse(1L, "Oxygen1", Type.BUILD);
            Tag tag2 = Tag.builder()
                    .id(2L)
                    .name("Oxygen2")
                    .type(Type.DLC)
                    .build();
            TagResponse res2 = new TagResponse(2L, "Oxygen2", Type.DLC);
            Tag tag3 = Tag.builder()
                    .id(3L)
                    .name("Oxygen3")
                    .type(Type.BUILD)
                    .build();
            TagResponse res3 = new TagResponse(3L, "Oxygen3", Type.BUILD);

            when(tagRepo.findAllByType(Type.BUILD)).thenReturn(List.of(tag1, tag3));
            when(tagMapper.toResponse(tag1)).thenReturn(res1);
            when(tagMapper.toResponse(tag3)).thenReturn(res3);

            List<TagResponse> result = tagService.findAllByType(Type.BUILD);

            assertThat(result)
                    .extracting(TagResponse::id)
                    .containsExactly(1L, 3L);
        }
    }

    @Nested
    @DisplayName("findAllByIds")
    class findAllByIds {
        @Test
        @DisplayName("findAllByIds returns all matching ids")
        void findAllByIdsReturnsAllMatchingIds() {
            Tag tag1 = Tag.builder()
                    .id(1L)
                    .name("Oxygen1")
                    .type(Type.BUILD)
                    .build();
            TagResponse res1 = new TagResponse(1L, "Oxygen1", Type.BUILD);
            Tag tag2 = Tag.builder()
                    .id(2L)
                    .name("Oxygen2")
                    .type(Type.DLC)
                    .build();
            TagResponse res2 = new TagResponse(2L, "Oxygen2", Type.DLC);
            Tag tag3 = Tag.builder()
                    .id(3L)
                    .name("Oxygen3")
                    .type(Type.BUILD)
                    .build();
            TagResponse res3 = new TagResponse(3L, "Oxygen3", Type.BUILD);

            when(tagRepo.findAllByIdIn(List.of(1L, 2L))).thenReturn(List.of(tag1, tag2));
            when(tagMapper.toResponse(tag1)).thenReturn(res1);
            when(tagMapper.toResponse(tag2)).thenReturn(res2);

            List<TagResponse> result = tagService.findAllByIds(List.of(1L, 2L));

            assertThat(result)
                    .extracting(TagResponse::id)
                    .containsExactly(1L, 2L);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll{
        @Test
        @DisplayName("findAll returns all mapped responses")
        void findAllReturnsAllMappedResponses(){
            Pageable pageable = PageRequest.of(0, 10);
            Tag tag = aDlcTagWithId(1L);
            Page<Tag> pageOfTags = new PageImpl<>(List.of(tag), pageable, 1);

            when(tagRepo.findAll(pageable)).thenReturn(pageOfTags);
            when(tagMapper.toResponse(tag)).thenReturn(aDlcTagResponse(1L));

            Page<TagResponse> result = tagService.findAll(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            verify(tagRepo).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("createTag")
    class CreateTag{

        @Test
        @DisplayName("createTag should map save and return response")
        void createTagShouldMapSaveAndReturnResponse(){
            TagRequest request = aValidTagRequest();
            Tag unsaved = aDlcTag();
            Tag saved = aDlcTagWithId(1L);
            TagResponse response = aDlcTagResponse(1L);

            when(tagMapper.toEntity(request)).thenReturn(unsaved);
            when(tagRepo.save(unsaved)).thenReturn(saved);
            when(tagMapper.toResponse(saved)).thenReturn(response);

            TagResponse actual = tagService.createTag(request);

            assertThat(actual).isEqualTo(response);
            verify(tagMapper).toEntity(request);
            verify(tagRepo).save(unsaved);
            verify(tagMapper).toResponse(saved);
        }
    }

    @Nested
    @DisplayName("updateTag")
    class UpdateTag{

        @Test
        @DisplayName("updateTag should load mutate and save when tag exists")
        void updateTagShouldLoadMutateAndSaveWhenTagExists(){
            Long tagId = 1L;
            TagRequest request = new TagRequest("Update name", Type.DLC);
            Tag existing = aDlcTagWithId(tagId);
            Tag afterSave = Tag.builder()
                    .id(tagId)
                    .name("Updated name")
                    .type(Type.DLC)
                    .build();
            TagResponse expected = new TagResponse(tagId, "Updated name", Type.DLC);

            when(tagRepo.findById(tagId)).thenReturn(Optional.of(existing));
            when(tagRepo.save(existing)).thenReturn(afterSave);
            when(tagMapper.toResponse(afterSave)).thenReturn(expected);

            TagResponse actual = tagService.updateTag(tagId, request);

            assertThat(actual).isEqualTo(expected);
            verify(tagMapper).updateToEntity(request, existing);
            verify(tagRepo).save(existing);
        }

        @Test
        @DisplayName("updateTag should throw when tag doesnt exist")
        void updateTagShouldThrowWhenTagDoesntExist(){
            TagRequest request = aValidTagRequest();

            when(tagRepo.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tagService.updateTag(999L, request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Tag not found");

            verify(tagRepo, never()).save(any());
            verifyNoInteractions(tagMapper);
        }
    }

    @Nested
    @DisplayName("deleteTag")
    class DeleteTag{

        @Test
        @DisplayName("deleteTag should delete and publish event when tag exists")
        void deleteTagShouldDeleteAndPublishEventWhenTagExists(){
            Long tagId = 1L;
            Tag existing = aDlcTagWithId(tagId);

            when(tagRepo.findById(tagId)).thenReturn(Optional.of(existing));

            String result = tagService.deleteTag(tagId);

            assertThat(result).isEqualTo("Tag deleted successfully");

            verify(tagRepo).delete(existing);

            ArgumentCaptor<DeleteEntityEvent> eventCaptor = ArgumentCaptor.forClass(DeleteEntityEvent.class);
            verify(rabbitTemplate).convertAndSend(
                    eq(RabbitMQConfig.TAG_EVENTS_EXCHANGE),
                    eq(""),
                    eventCaptor.capture()
            );
            DeleteEntityEvent captured = eventCaptor.getValue();
            assertThat(captured.serviceName()).isEqualTo("tag");
            assertThat(captured.id()).isEqualTo(tagId);
        }

        @Test
        @DisplayName("deleteTag should throw when tag does not exist")
        void deleteTagShouldThrowWhenTagDoesntExist(){
            when(tagRepo.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tagService.deleteTag(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Tag not found");

            verify(tagRepo, never()).delete(any());
            verifyNoInteractions(rabbitTemplate);
        }
    }
}
