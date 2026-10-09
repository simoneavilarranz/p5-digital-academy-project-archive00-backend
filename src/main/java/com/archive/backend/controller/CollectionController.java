package com.archive.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.archive.backend.service.CollectionService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/collections")
@RequiredArgsConstructor  
public class CollectionController {
    private final CollectionService collectionService;
}
