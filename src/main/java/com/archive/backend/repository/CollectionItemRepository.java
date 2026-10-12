package com.archive.backend.repository;

import java.util.List;
import java.util.Optional;

import com.archive.backend.entity.CollectionType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.archive.backend.entity.CollectionItem;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {
    List<CollectionItem> findByUserId(Long userId);

    List<CollectionItem> findByUserIdAndType(Long userId, CollectionType type);

    Optional<CollectionItem> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndLastFmIdAndType(Long userId, String lastFmId, CollectionType type);
}
