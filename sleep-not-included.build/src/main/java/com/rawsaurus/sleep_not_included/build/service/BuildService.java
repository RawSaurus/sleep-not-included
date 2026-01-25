package com.rawsaurus.sleep_not_included.build.service;

import com.rawsaurus.sleep_not_included.build.dto.BuildRequest;
import com.rawsaurus.sleep_not_included.build.dto.BuildResponse;
import com.rawsaurus.sleep_not_included.build.mapper.BuildMapper;
import com.rawsaurus.sleep_not_included.build.model.Build;
import com.rawsaurus.sleep_not_included.build.repo.BuildRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BuildService {

    private final BuildRepository buildRepo;
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
        return buildRepo.findAllByUserId(userId, pageable)
                .map(buildMapper::toResponse);
    }

    public BuildResponse createBuild(Long userId, BuildRequest request){
        Build build = buildMapper.toEntity(request);
        build.setUserId(userId);
        return buildMapper.toResponse(buildRepo.save(build));
    }

    public BuildResponse updateBuild(Long userId, Long buildId, BuildRequest request){
        return null;
    }

    public String deleteBuild(Long userId, Long buildId){
        return null;
    }
}
