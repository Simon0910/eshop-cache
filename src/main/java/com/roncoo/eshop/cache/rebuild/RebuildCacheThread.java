package com.roncoo.eshop.cache.rebuild;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZooKeeperSession;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 缓存重建线程
 *
 * @author Administrator
 */
@Slf4j
public class RebuildCacheThread implements Runnable {

    public void run() {
        log.info("Rebuild Cache Queue Ready......");
        RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
        ZooKeeperSession zkSession = ZooKeeperSession.getInstance();
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext()
                .getBean("cacheService");

        while (true) {
            ProductInfo productInfo = rebuildCacheQueue.takeProductInfo();

            zkSession.acquireDistributedLock(productInfo.getId());

            ProductInfo existedProductInfo = cacheService.getProductInfoFromRedisCache(productInfo.getId());

            if (existedProductInfo != null) {
                // 比较当前数据的时间版本比已有数据的时间版本是新还是旧
                try {
                    Date date = productInfo.getUpdateTime();
                    Date existedDate = productInfo.getUpdateTime();

                    if (date.before(existedDate)) {
                        System.out.println("current date[" + productInfo.getUpdateTime() + "] is before existed date[" + existedProductInfo.getUpdateTime() + "]");
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("current date[" + productInfo.getUpdateTime() + "] is after existed date[" + existedProductInfo.getUpdateTime() + "]");
            } else {
                System.out.println("ProductInfo not exist from redis");
            }

            cacheService.saveProductInfo2RedisCache(productInfo);
        }
    }

}
