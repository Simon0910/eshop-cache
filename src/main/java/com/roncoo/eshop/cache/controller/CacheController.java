package com.roncoo.eshop.cache.controller;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.prewarm.CachePreWarmThread;
import com.roncoo.eshop.cache.rebuild.RebuildCacheQueue;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.service.EhCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;

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
        ProductInfo productInfoCache = cacheService.getProductInfoFromRedisCache(productId);
        if (productInfoCache == null) {
            productInfoCache = cacheService.getProductInfoFromLocalCache(productId);

            if (productInfoCache == null) {
                // 就需要从数据源重新拉去数据，重建缓存
                Date now = new Date();
                long updateTimeNow = now.getTime();
                String productInfoJSON = "{\"id\":  " + productId + " , \"name\": \"iphone7手机Rebuild\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"updateTime\": \"" + updateTimeNow + "\"}";
                ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
                // 将数据推送到一个内存队列中
                RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
                rebuildCacheQueue.putProductInfo(productInfo);
                return productInfo;
            } else {

            }
        }
        return productInfoCache;
    }


    @RequestMapping("/getShopInfo")
    @ResponseBody
    public ShopInfo getShopInfo(Long shopId) {
        ShopInfo shopInfoFromRedisCache = cacheService.getShopInfoFromRedisCache(shopId);
        if (shopInfoFromRedisCache == null) {

        }
        return shopInfoFromRedisCache;
    }


    @RequestMapping("/preWarmCache")
    @ResponseBody
    public void preWarmCache() {
        new CachePreWarmThread().start();
    }
}
