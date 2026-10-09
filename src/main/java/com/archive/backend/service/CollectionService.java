package com.archive.backend.service;

import org.springframework.stereotype.Service;

import com.archive.backend.dto.CollectionRequest;
import com.archive.backend.dto.CollectionResponse;
import com.archive.backend.entity.CollectionItem;
import com.archive.backend.entity.User;
import com.archive.backend.repository.CollectionItemRepository;
import com.archive.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final UserRepository userRepository;
    private final CollectionItemRepository collectionItemRepository;

    public CollectionResponse addToCollection(CollectionRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        boolean exists = collectionItemRepository.existsByUserIdAndLastFmIdAndType(
            user.getId(), request.lastFmId(), request.type()
        );

        if (exists) {
            throw new RuntimeException("Item already in collection");
        }

        CollectionItem item = CollectionItem.builder()
            .user(user)
            .lastFmId(request.lastFmId())
            .albumName(request.albumName())
            .artistName(request.artistName())
            .coverUrl(request.coverUrl())
            .type(request.type())
            .rating(request.rating())
            .build();

        collectionItemRepository.save(item);

        return mapToResponse(item);
    }

    private CollectionResponse mapToResponse(CollectionItem item) {
        return new CollectionResponse(
            item.getId(),
            item.getLastFmId(),
            item.getAlbumName(),
            item.getArtistName(),
            item.getCoverUrl(),
            item.getType(),
            item.getRating(),
            item.getReviewText(),
            item.getCreatedAt()
        );
    }
}