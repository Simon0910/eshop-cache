package com.roncoo.eshop.cache.controller;

import com.alibaba.fastjson.JSON;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.service.EhCacheService;
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
@Controller
public class CacheController {

    @Resource
    private EhCacheService ehCacheService;

    @Resource
    private CacheService cacheService;

    @RequestMapping("/putLocalCache")
    @ResponseBody
    public String putLocalCache(ProductInfo productInfo) {
        ehCacheService.saveLocalCache(String.valueOf(productInfo.getId()), JSON.toJSONString(productInfo));
        return "SUCCESS";
    }

    @RequestMapping("/getLocalCache")
    @ResponseBody
    public ProductInfo getLocalCache(Long id) {
        String localCache = ehCacheService.getLocalCache(String.valueOf(id));
        if (StringUtils.isEmpty(localCache)) {
            return null;
        }
        return JSON.parseObject(localCache, ProductInfo.class);
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
