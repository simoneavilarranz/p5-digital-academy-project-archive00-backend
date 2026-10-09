package com.archive.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.archive.backend.dto.CollectionRequest;
import com.archive.backend.dto.CollectionResponse;
import com.archive.backend.dto.ReviewRequest;
import com.archive.backend.entity.CollectionItem;
import com.archive.backend.entity.User;
import com.archive.backend.repository.CollectionItemRepository;
import com.archive.backend.repository.UserRepository;
import com.archive.backend.entity.CollectionType;

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

    public List<CollectionResponse> getUserCollection(String username, CollectionType type) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    List<CollectionItem> items = (type != null)
        ? collectionItemRepository.findByUserIdAndType(user.getId(), type)
        : collectionItemRepository.findByUserId(user.getId());

    return items.stream()
        .map(this::mapToResponse)
        .toList();
    }

    public void removeFromCollection(Long id, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        CollectionItem item = collectionItemRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Item not found"));

        collectionItemRepository.delete(item);
    }

    public CollectionResponse updateReview(Long id, String username, ReviewRequest request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        CollectionItem item = collectionItemRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setReviewText(request.reviewText());
        collectionItemRepository.save(item);

        return mapToResponse(item);
    }
}