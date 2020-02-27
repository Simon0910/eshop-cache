package com.roncoo.eshop.cache.rebuild;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZooKeeperSession;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 缓存重建线程
 * // TODO: 2020/2/21   缓存重建 & Kaka缓存更新 不在同一台缓存实例 导致ehcache不一致怎么办?
 * // TODO: 2020/2/21   能否Kafka消息 -> 到 nginx 走和缓存重建相同的ngixn路由规则?
 *
 * @author Administrator
 */
@Slf4j
public class RebuildCacheThread implements Runnable {

    public void run() {
        log.info("Rebuild Cache Queue Waiting Receive ProductInfo......");
        RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
        ZooKeeperSession zkSession = ZooKeeperSession.getInstance();
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");

        while (true) {
            ProductInfo productInfo = rebuildCacheQueue.takeProductInfo();

            zkSession.acquireDistributedLock(productInfo.getId());

            try {
                ProductInfo existedProductInfo = cacheService.getProductInfoFromRedisCache(productInfo.getId());

                if (existedProductInfo != null) {
                    // 比较当前数据的时间版本比已有数据的时间版本是新还是旧
                    try {
                        Date date = productInfo.getUpdateTime();
                        Date existedDate = productInfo.getUpdateTime();

                        if (date.before(existedDate)) {
                            log.info("current date[" + productInfo.getUpdateTime() + "] is before existed date[" + existedProductInfo.getUpdateTime() + "]");
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    log.info("current date[" + productInfo.getUpdateTime() + "] is after existed date[" + existedProductInfo.getUpdateTime() + "]");
                } else {
                    log.info("ProductInfo not exist from redis");
                }

                // try {
                //     Thread.sleep(10 * 1000);
                // } catch (InterruptedException e) {
                //     e.printStackTrace();
                // }

                cacheService.saveProductInfo2LocalCache(productInfo);
                cacheService.saveProductInfo2RedisCache(productInfo);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                zkSession.releaseDistributedLock(productInfo.getId());
            }
        }
    }


}
