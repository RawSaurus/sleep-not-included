package com.rawsaurus.sleep_not_included.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rawsaurus.sleep_not_included.tag.controller.TagController;
import com.rawsaurus.sleep_not_included.tag.dto.TagResponse;
import com.rawsaurus.sleep_not_included.tag.model.Type;
import com.rawsaurus.sleep_not_included.tag.security.InternalApiKeyFilter;
import com.rawsaurus.sleep_not_included.tag.security.SecurityConfig;
import com.rawsaurus.sleep_not_included.tag.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

import static com.rawsaurus.sleep_not_included.tag.TagTestFixtures.aDlcTagResponse;
import static com.rawsaurus.sleep_not_included.tag.TagTestFixtures.aValidTagRequest;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "sni.security.client-id-frontend=test-client")
@ActiveProfiles("test")
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private InternalApiKeyFilter internalApiKeyFilter;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Qualifier("requestMappingHandlerMapping")
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    void debugMappings() {
        handlerMapping.getHandlerMethods().forEach((mapping, method) ->
                System.out.println(mapping + " -> " + method));
    }

    @Nested
    @DisplayName("GET endpoints")
    class GetEndpoints{

        @Test
        void findById_shouldReturn200_withTagBody()throws Exception{
            when(tagService.findById(1L)).thenReturn(aDlcTagResponse(1L));

            System.out.println("Mock result: " + tagService.findById(1L));

            mockMvc.perform(get("/tag/1").accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Spaced Out"))
                    .andExpect(jsonPath("$.type").value("DLC"));
        }

        @Test
        void findById_shouldReturn404_whenTagNotFound() throws Exception{
            when(tagService.findById(999L)).thenThrow(new EntityNotFoundException("Tag not found"));

            mockMvc.perform(get("/tag/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void findByName_shouldReturn200() throws Exception {
            when(tagService.findByName("Spaced Out")).thenReturn(aDlcTagResponse(1L));

            mockMvc.perform(get("/tag/name/Spaced Out").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Spaced Out"));
        }

        @Test
        void findAllByType_shouldReturn200_withList() throws Exception{
            when(tagService.findAllByType(Type.DLC)).thenReturn(
                    List.of(aDlcTagResponse(1L), aDlcTagResponse(2L))
            );

            mockMvc.perform(get("/tag/type/DLC").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].type").value(Type.DLC));
        }

        @Test
        void findAllByType_shouldReturn400_whenTypeIsInvalid() throws Exception{
            mockMvc.perform(get("tag/type/INVALID_TYPE").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(tagService);
        }

        @Test
        void findAll_shouldUseDefaultPagination_whenParamsAbsent() throws Exception{
            Page<TagResponse> page = new PageImpl<>(List.of(aDlcTagResponse(1L)));
            when(tagService.findAll(any())).thenReturn(page);

            mockMvc.perform(get("/tag").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));
        }

        @Test
        void findAllByIds_shouldBindQueryParamList() throws Exception{
            when(tagService.findAllByIds(List.of(1L, 2L, 3L)))
                    .thenReturn(List.of(aDlcTagResponse(1L)));

            mockMvc.perform(get("/tag/find-all-by-ids")
                        .param("ids", "1", "2", "3")
                        .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(tagService).findAllByIds(List.of(1L, 2L, 3L));
        }
    }

    @Nested
    @DisplayName("POST endpoints")
    class PostEndpoints{

        @Test
        void createTag_shouldReturn401_whenUnauthenticated() throws Exception{
            mockMvc.perform(post("/tag")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aValidTagRequest())))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(tagService);
        }

        @Test
        void createTag_shouldReturn403_whenAuthenticatedButNotAdmin() throws Exception{
            mockMvc.perform(
                    post("/tag")
                            .with(csrf())
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(aValidTagRequest())))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(tagService);
        }
    }
}
