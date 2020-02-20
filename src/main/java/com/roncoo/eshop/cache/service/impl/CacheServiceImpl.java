package com.roncoo.eshop.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.service.EhCacheService;
import com.roncoo.eshop.cache.service.RedisCacheService;
import com.roncoo.eshop.cache.service.keys.ProductKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 缓存Service实现类
 *
 * @author Administrator
 */
@Slf4j
@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    @Resource
    EhCacheService ehCacheService;

    @Resource
    private RedisCacheService redisCacheService;


    // ======================ehcache========================

    /**
     * 将商品信息保存到本地的ehcache缓存中
     *
     * @param productInfo
     */
    // @CachePut(value = CACHE_NAME, key = "'" + PRODUCT_INFO + "' + #productInfo.id")
    public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
        ehCacheService.saveLocalCache(ProductKey.productInfo.generateKey(String.valueOf(productInfo.getId())), JSONObject.toJSONString(productInfo));
        return productInfo;
    }

    /**
     * 从本地ehcache缓存中获取商品信息
     *
     * @param productId
     * @return
     */
    // @Cacheable(value = CACHE_NAME, key = "'" + PRODUCT_INFO + "' + #productId")
    public ProductInfo getProductInfoFromLocalCache(Long productId) {
        String localCache = ehCacheService.getLocalCache(ProductKey.productInfo.generateKey(String.valueOf(productId)));
        log.info("从localCache缓存中获取 key = {}, ProductInfo == {}", ProductKey.productInfo.generateKey(String.valueOf(productId)), localCache);
        return JSONObject.parseObject(localCache, ProductInfo.class);
    }

    /**
     * 将店铺信息保存到本地的ehcache缓存中
     *
     * @param shopInfo
     */
    // @CachePut(value = CACHE_NAME, key = "'shop_info:' + #shopInfo.getId()")
    public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
        ehCacheService.saveLocalCache(ProductKey.shopInfo.generateKey(String.valueOf(shopInfo.getId())), JSONObject.toJSONString(shopInfo));
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
        String localCache = ehCacheService.getLocalCache(ProductKey.shopInfo.generateKey(String.valueOf(shopId)));
        log.info("从localCache缓存中获取 key = {}, ShopInfo == {}", ProductKey.shopInfo.generateKey(String.valueOf(shopId)), localCache);
        return JSONObject.parseObject(localCache, ShopInfo.class);
    }


    // ======================redis========================

    /**
     * 将商品信息保存到redis中
     *
     * @param productInfo
     */
    public void saveProductInfo2RedisCache(ProductInfo productInfo) {
        redisCacheService.set(ProductKey.productInfo, String.valueOf(productInfo.getId()), productInfo);
    }

    @Override
    public ProductInfo getProductInfoFromRedisCache(Long productId) {
        ProductInfo productInfo = redisCacheService.get(ProductKey.productInfo, String.valueOf(productId), ProductInfo.class);
        log.info("从redis缓存中获取 key = {}, ProductInfo == {}", ProductKey.productInfo.generateKey(String.valueOf(productId)), JSON.toJSONString(productInfo));
        return productInfo;
    }

    /**
     * 将店铺信息保存到redis中
     *
     * @param shopInfo
     */
    public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
        redisCacheService.set(ProductKey.shopInfo, String.valueOf(shopInfo.getId()), shopInfo);
    }

    @Override
    public ShopInfo getShopInfoFromRedisCache(Long shopId) {
        ShopInfo shopInfo = redisCacheService.get(ProductKey.productInfo, String.valueOf(shopId), ShopInfo.class);
        log.info("从redis缓存中获取 key = {}, ProductInfo == {}", ProductKey.productInfo.generateKey(String.valueOf(shopId)), JSON.toJSONString(shopInfo));
        return shopInfo;
    }

}
