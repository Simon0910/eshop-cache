package com.roncoo.eshop.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.roncoo.eshop.cache.model.ProductInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class ProductServiceGetCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    public ProductServiceGetCommand(Long productId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductService"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(10)
                        .withMaximumSize(30)
                        .withAllowMaximumSizeToDivergeFromCoreSize(true)
                        .withKeepAliveTimeMinutes(1)
                        .withMaxQueueSize(50)
                        .withQueueSizeRejectionThreshold(100))
        );
        this.productId = productId;
    }

    /**
     * nginx 缓存失效, 避免可能同时并发打到后台redis
     * 设置nginx 随机缓存时间
     * math.randomseed(tostring(os.time()):reverse():sub(1, 7))
     * local expireTime = math.random(600, 1200)
     *
     * @return
     * @throws Exception
     */
    @Override
    protected ProductInfo run() throws Exception {
        // 发送http或rpc接口调用，去调用商品服务的接口
        String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"updateTime\": \"2017-01-01 12:01:00\"}";
        log.info("ProductServiceGetCommand - data");
        if (productId == 100) {
            // 缓存穿透
            // 我们在这里模拟的是说，从源头服务某个商品id没有查询到数据，我们这里写死了，比如就是proudctId=100
            // 在实际的生产环境中，我们其实是如果没有查询到数据，就给返回这么一个商品信息就可以了
            ProductInfo productInfo = new ProductInfo();
            productInfo.setId(productId);
            productInfo.setUpdateTime(new Date());
            return productInfo;
        }
        return JSONObject.parseObject(productInfoJSON, ProductInfo.class);
    }

    @Override
    protected ProductInfo getFallback() {
        HBaseColdDataCommand command = new HBaseColdDataCommand(productId);
        return command.execute();
    }

    private class HBaseColdDataCommand extends HystrixCommand<ProductInfo> {

        private Long productId;

        public HBaseColdDataCommand(Long productId) {
            super(HystrixCommandGroupKey.Factory.asKey("HBaseGroup"));
            this.productId = productId;
        }

        @Override
        protected ProductInfo run() throws Exception {
            // 查询hbase
            String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"updateTime\": \"2017-01-01 12:01:00\"}";
            System.out.println("HBaseColdDataCommand - data");
            return JSONObject.parseObject(productInfoJSON, ProductInfo.class);
        }

        @Override
        protected ProductInfo getFallback() {
            ProductInfo productInfo = new ProductInfo();
            productInfo.setId(productId);
            productInfo.setName("HBaseColdData 降级");
            productInfo.setUpdateTime(new Date());
            // 从内存中找一些残缺的数据拼装进去
            return productInfo;
        }

    }
}
