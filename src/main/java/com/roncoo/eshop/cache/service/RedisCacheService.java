package com.roncoo.eshop.cache.service;

import com.alibaba.fastjson.JSON;
import com.roncoo.eshop.cache.service.keys.KeyPrefix;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;

@Service
public class RedisCacheService {

    @Resource
    private JedisCluster jedisCluster;

    private static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     * 获取当个对象
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        String jsonStr = jedisCluster.get(prefix.generateKey(key));
        return stringToBean(jsonStr, clazz);
    }

    /**
     * 设置对象
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        String str = beanToString(value);
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        int seconds = prefix.expireSeconds();
        if (seconds <= 0) {
            jedisCluster.set(prefix.generateKey(key), str);
        } else {
            jedisCluster.setex(prefix.generateKey(key), seconds, str);
        }
        return true;
    }

}
