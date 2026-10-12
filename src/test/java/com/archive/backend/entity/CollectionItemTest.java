package com.archive.backend.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CollectionItemTest {

    @Test
    void onCreate_shouldSetCreatedAt() {
        CollectionItem item = new CollectionItem();
        assertThat(item.getCreatedAt()).isNull();

        item.onCreate();

        assertThat(item.getCreatedAt()).isNotNull();
    }

    @Test
    void builder_shouldCreateValidItem() {
        CollectionItem item = CollectionItem.builder()
            .albumName("OK Computer")
            .artistName("Radiohead")
            .type(CollectionType.SAVED)
            .rating(5)
            .build();

        assertThat(item.getAlbumName()).isEqualTo("OK Computer");
        assertThat(item.getArtistName()).isEqualTo("Radiohead");
        assertThat(item.getType()).isEqualTo(CollectionType.SAVED);
        assertThat(item.getRating()).isEqualTo(5);
    }
}