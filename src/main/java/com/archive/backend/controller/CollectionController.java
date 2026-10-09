package com.archive.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.archive.backend.dto.CollectionRequest;
import com.archive.backend.dto.CollectionResponse;
import com.archive.backend.entity.CollectionType;
import com.archive.backend.service.CollectionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/collections")
@RequiredArgsConstructor  
public class CollectionController {
    private final CollectionService collectionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CollectionResponse addToCollection(
            @Valid @RequestBody CollectionRequest request,
            @AuthenticationPrincipal  UserDetails userDetails
    ) {
        return collectionService.addToCollection(request, userDetails.getUsername());
    }

    @GetMapping("/me")
    public List<CollectionResponse> getMyCollection(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) CollectionType type
    ) {
        return collectionService.getUserCollection(userDetails.getUsername(), type);
    }
}
