package com.roncoo.eshop.cache.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.service.EhCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;

/**
 * 缓存Service实现类
 *
 * @author Administrator
 */
@Slf4j
@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    public static final String CACHE_NAME = "local";
    public static final String PRODUCT_INFO = "product_info:";
    public static final String SHOP_INFO = "shop_info:";

    @Resource
    EhCacheService ehCacheService;

    @Resource
    private JedisCluster jedisCluster;


    // ======================ehcache========================

    /**
     * 将商品信息保存到本地的ehcache缓存中
     *
     * @param productInfo
     */
    @CachePut(value = CACHE_NAME, key = "'" + PRODUCT_INFO + "' + #productInfo.id")
    public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    /**
     * 从本地ehcache缓存中获取商品信息
     *
     * @param productId
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "'" + PRODUCT_INFO + "' + #productId")
    public ProductInfo getProductInfoFromLocalCache(Long productId) {
        return null;
    }

    /**
     * 将店铺信息保存到本地的ehcache缓存中
     *
     * @param shopInfo
     */
//	@CachePut(value = CACHE_NAME, key = "'shop_info:' + #shopInfo.getId()")
    public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
        ehCacheService.saveLocalCache(SHOP_INFO + shopInfo.getId(), JSONObject.toJSONString(shopInfo));
        return shopInfo;
    }

    /**
     * 从本地ehcache缓存中获取店铺信息
     *
     * @param shopId
     * @return
     */
//	@Cacheable(value = CACHE_NAME, key = "'shop_info:' + #shopId")
    public ShopInfo getShopInfoFromLocalCache(Long shopId) {
        String localCache = ehCacheService.getLocalCache(SHOP_INFO + shopId);
        log.info("从localCache缓存中获取 key = {}, ShopInfo == {}", SHOP_INFO + shopId, localCache);
        return JSONObject.parseObject(localCache, ShopInfo.class);
    }


    // ======================redis========================

    /**
     * 将商品信息保存到redis中
     *
     * @param productInfo
     */
    public void saveProductInfo2RedisCache(ProductInfo productInfo) {
        String key = PRODUCT_INFO + productInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(productInfo));
    }

    @Override
    public ProductInfo getProductInfoFromRedisCache(Long productId) {
        String key = PRODUCT_INFO + productId;
        String jsonStr = jedisCluster.get(key);
        log.info("从redis缓存中获取 key = {}, ProductInfo == {}", key, jsonStr);
        if (StringUtils.isEmpty(jsonStr)) {
            return null;
        }
        return JSONObject.parseObject(jsonStr, ProductInfo.class);
    }

    /**
     * 将店铺信息保存到redis中
     *
     * @param shopInfo
     */
    public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
        String key = SHOP_INFO + shopInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(shopInfo));
    }

    @Override
    public ShopInfo getShopInfoFromRedisCache(Long shopId) {
        String key = SHOP_INFO + shopId;
        String jsonStr = jedisCluster.get(key);
        log.info("从redis缓存中获取 key = {}, ShopInfo == {}", key, jsonStr);
        if (StringUtils.isEmpty(jsonStr)) {
            return null;
        }
        return JSONObject.parseObject(jsonStr, ShopInfo.class);
    }

}
