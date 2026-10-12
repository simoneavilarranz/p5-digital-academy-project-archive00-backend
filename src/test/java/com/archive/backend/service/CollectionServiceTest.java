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
}