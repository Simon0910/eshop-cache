package com.roncoo.eshop.cache.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EhCacheService {

    public static final String CACHE_NAME = "local";

    /**
     * 添加 ehcache 缓存
     *
     * @param cacheKey
     * @param jsonStr
     * @return
     */
    @CachePut(value = CACHE_NAME, key = "#cacheKey")
    public String saveLocalCache(String cacheKey, String jsonStr) {
        return jsonStr;
    }

    /**
     * 获取 ehcache 缓存
     *
     * @param cacheKey
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "#cacheKey")
    public String getLocalCache(String cacheKey) {
        return null;
    }
}
