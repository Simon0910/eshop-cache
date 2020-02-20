package com.roncoo.eshop.cache.service.keys;

public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();

    String generateKey(String id);
}
