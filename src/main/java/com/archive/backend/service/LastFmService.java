package com.archive.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Service 
@RequiredArgsConstructor 
public class LastFmService {
    
    @Value("${lastfm.api.key}")
    private String apiKey;

    @Value ("${lastfm.api.url}")
    private String apiUrl;

    private final RestClient restClient;

}
