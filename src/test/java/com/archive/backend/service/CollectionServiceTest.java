package com.archive.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.archive.backend.dto.CollectionRequest;
import com.archive.backend.dto.CollectionResponse;
import com.archive.backend.dto.ReviewRequest;
import com.archive.backend.entity.CollectionItem;
import com.archive.backend.entity.CollectionType;
import com.archive.backend.entity.User;
import com.archive.backend.repository.CollectionItemRepository;
import com.archive.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CollectionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollectionItemRepository collectionItemRepository;

    private CollectionService collectionService;

    @BeforeEach
    void setUp() {
        collectionService = new CollectionService(userRepository, collectionItemRepository);
    }

    @Test
    void addToCollection_shouldSaveItem_whenDataIsValid() {
        String username = "testuser";
        User user = User.builder().id(1L).username(username).build();
        CollectionRequest request = new CollectionRequest(
            "Radiohead/OK Computer",
            "OK Computer",
            "Radiohead",
            "cover.png",
            CollectionType.SAVED,
            5,
            "Amazing album"
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(collectionItemRepository.existsByUserIdAndLastFmIdAndType(
            1L, "Radiohead/OK Computer", CollectionType.SAVED)).thenReturn(false);
        when(collectionItemRepository.save(any(CollectionItem.class))).thenAnswer(inv -> inv.getArgument(0));

        CollectionResponse response = collectionService.addToCollection(request, username);

        assertThat(response.albumName()).isEqualTo("OK Computer");
        assertThat(response.artistName()).isEqualTo("Radiohead");
        assertThat(response.rating()).isEqualTo(5);
        assertThat(response.reviewText()).isEqualTo("Amazing album");
        assertThat(response.type()).isEqualTo(CollectionType.SAVED);
        verify(collectionItemRepository).save(any(CollectionItem.class));
    }

    @Test
    void addToCollection_shouldThrow_whenItemAlreadyExists() {
        String username = "testuser";
        User user = User.builder().id(1L).username(username).build();
        CollectionRequest request = new CollectionRequest(
            "Radiohead/OK Computer", "OK Computer", "Radiohead",
            "cover.png", CollectionType.SAVED, 5, "Amazing"
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(collectionItemRepository.existsByUserIdAndLastFmIdAndType(
            1L, "Radiohead/OK Computer", CollectionType.SAVED)).thenReturn(true);

        assertThatThrownBy(() -> collectionService.addToCollection(request, username))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Item already in collection");
    }

    @Test
    void getUserCollection_shouldReturnAllItems_whenTypeIsNull() {
        String username = "testuser";
        User user = User.builder().id(1L).username(username).build();
        CollectionItem item1 = CollectionItem.builder().id(1L).albumName("Album 1").build();
        CollectionItem item2 = CollectionItem.builder().id(2L).albumName("Album 2").build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(collectionItemRepository.findByUserId(1L)).thenReturn(List.of(item1, item2));

        List<CollectionResponse> result = collectionService.getUserCollection(username, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).albumName()).isEqualTo("Album 1");
        assertThat(result.get(1).albumName()).isEqualTo("Album 2");
    }
}