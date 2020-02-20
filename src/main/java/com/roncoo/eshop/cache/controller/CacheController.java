package com.roncoo.eshop.cache.controller;

import com.alibaba.fastjson.JSON;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.service.EhCacheService;
import com.roncoo.eshop.cache.service.impl.CacheServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 缓存Controller
 *
 * @author Administrator
 */
@Slf4j
@Controller
public class CacheController {

    @Resource
    private EhCacheService ehCacheService;

    @Resource
    private CacheService cacheService;

    @RequestMapping("/putLocalCache")
    @ResponseBody
    public String putLocalCache(String cacheKey, String jsonStr) {
        ehCacheService.saveLocalCache(cacheKey, jsonStr);
        return "SUCCESS";
    }

    @RequestMapping("/getLocalCache")
    @ResponseBody
    public String getLocalCache(String cacheKey) {
        String localCache = ehCacheService.getLocalCache(cacheKey);
        log.info("从localCache缓存中获取 key = {}, localCache == {}", cacheKey, localCache);
        return localCache;
    }


    @RequestMapping("/getProductInfoFromLocalCache")
    @ResponseBody
    public ProductInfo getProductInfoFromLocalCache(Long productId) {
        ProductInfo productInfoFromLocalCache = cacheService.getProductInfoFromLocalCache(productId);
        log.info("从localCache缓存中获取 key = {}, ProductInfo == {}", CacheServiceImpl.PRODUCT_INFO + productId, JSON.toJSONString(productInfoFromLocalCache));
        if (productInfoFromLocalCache == null) {

        }
        return productInfoFromLocalCache;
    }

    @RequestMapping("/getShopInfoFromLocalCache")
    @ResponseBody
    public ShopInfo getShopInfoFromLocalCache(Long shopId) {
        ShopInfo shopInfoFromLocalCache = cacheService.getShopInfoFromLocalCache(shopId);
        if (shopInfoFromLocalCache == null) {

        }
        return shopInfoFromLocalCache;
    }


    @RequestMapping("/getProductInfo")
    @ResponseBody
    public ProductInfo getProductInfo(Long productId) {
        ProductInfo productInfoFromRedisCache = cacheService.getProductInfoFromRedisCache(productId);
        if (productInfoFromRedisCache == null) {

        }
        return productInfoFromRedisCache;
    }

    @RequestMapping("/getShopInfo")
    @ResponseBody
    public ShopInfo getShopInfo(Long shopId) {
        ShopInfo shopInfoFromRedisCache = cacheService.getShopInfoFromRedisCache(shopId);
        if (shopInfoFromRedisCache == null) {

        }
        return shopInfoFromRedisCache;
    }

}
