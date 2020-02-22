package com.roncoo.eshop.cache.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZooKeeperSession;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 缓存预热线程
 *
 * @author Administrator
 */
public class CachePreWarmThread extends Thread {

    @Override
    public void run() {
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");
        ZooKeeperSession zkSession = ZooKeeperSession.getInstance();

        // 获取storm taskId列表
        String taskIdList = zkSession.getNodeData("/taskId-list");

        if (!StringUtils.isEmpty(taskIdList)) {
            String[] taskIdListSplit = taskIdList.split(",");
            for (String taskId : taskIdListSplit) {
                String taskIdLockPath = "/taskId-lock-" + taskId;
                boolean result = zkSession.acquireFastFailedDistributedLock(taskIdLockPath);
                if (!result) {
                    continue;
                }

                try {
                    String taskIdStatusLockPath = "/taskId-status-lock-" + taskId;
                    zkSession.acquireDistributedLock(taskIdStatusLockPath);

                    try {
                        String taskIdStatus = zkSession.getNodeData("/taskId-status-" + taskId);
                        if ("".equals(taskIdStatus)) {
                            String productIdList = zkSession.getNodeData("/task-hot-product-list-" + taskId);
                            JSONArray productIdJSONArray = JSONArray.parseArray(productIdList);

                            for (int i = 0; i < productIdJSONArray.size(); i++) {
                                Date now = new Date();
                                Long productId = productIdJSONArray.getLong(i);
                                String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"updateTime\": \"" + now.getTime() + "\"}";
                                ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
                                cacheService.saveProductInfo2LocalCache(productInfo);
                                cacheService.saveProductInfo2RedisCache(productInfo);
                            }

                            zkSession.setNodeData(taskIdStatusLockPath, "success");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        zkSession.releaseDistributedLock(taskIdStatusLockPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    zkSession.releaseDistributedLock(taskIdLockPath);
                }

            }
        }
    }

}
