package com.roncoo.eshop.cache.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

@Slf4j
public class RedisCacheGetCommand extends HystrixCommand<String> {

    private JedisCluster jedisCluster;

    private String key;

    public RedisCacheGetCommand(JedisCluster jedisCluster, String key) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(100)
                        .withCircuitBreakerRequestVolumeThreshold(1000)
                        .withCircuitBreakerErrorThresholdPercentage(70)
                        .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000))
        );
        this.jedisCluster = jedisCluster;
        this.key = key;
    }

    @Override
    protected String run() throws Exception {
        String value = jedisCluster.get(key);
        log.info("Hystrix - get {} form redis", key);
        return value;
    }

    @Override
    protected String getFallback() {
        log.info("Hystrix降级 - get key = {} form redis", key);
        return null;
    }
}
