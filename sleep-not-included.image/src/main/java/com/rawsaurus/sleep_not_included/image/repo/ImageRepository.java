package com.rawsaurus.sleep_not_included.image.repo;

import com.rawsaurus.sleep_not_included.image.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findImageByStoragePathAndOwnerId(String storagePath, Long ownerId);

}
