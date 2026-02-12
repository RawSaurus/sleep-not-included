package com.rawsaurus.sleep_not_included.image.service;

import com.rawsaurus.sleep_not_included.image.dto.ImageResponse;
import com.rawsaurus.sleep_not_included.image.mapper.ImageMapper;
import com.rawsaurus.sleep_not_included.image.repo.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository userRepo;
    private final ImageMapper userMapper;

    public ImageResponse findById(Long id){
        return null;
    }

    public ImageResponse findByOwnerId(Long ownerId){
        return null;
    }
}
