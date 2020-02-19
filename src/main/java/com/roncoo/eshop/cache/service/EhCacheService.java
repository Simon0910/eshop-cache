package com.roncoo.eshop.cache.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EhCacheService {
    public static final String CACHE_NAME = "local";

    @CachePut(value = CACHE_NAME, key = "#cacheKey")
    public String saveLocalCache(String cacheKey, String jsonStr) {
        return jsonStr;
    }
    @Cacheable(value = CACHE_NAME, key = "#cacheKey")
    public String getLocalCache(String cacheKey) {
        return null;
    }
}
