package com.roncoo.eshop.cache.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

@Slf4j
public class RedisCacheSetCommand extends HystrixCommand<Boolean> {

    private JedisCluster jedisCluster;

    private String key;
    private String value;
    private int expire;

    public RedisCacheSetCommand(JedisCluster jedisCluster, String key, String value, int expire) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(100)
                        .withCircuitBreakerRequestVolumeThreshold(1000)
                        .withCircuitBreakerErrorThresholdPercentage(70)
                        .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000))
        );
        this.jedisCluster = jedisCluster;
        this.key = key;
        this.value = value;
        this.expire = expire;
    }

    @Override
    protected Boolean run() throws Exception {
        if (expire > 0) {
            jedisCluster.set(key, value);
            log.info("Hystrix - set {} = {} to redis", key, value);
            jedisCluster.expire(key, expire);
            log.info("Hystrix - expire {}, {} to redis", key, expire);
        } else {
            jedisCluster.set(key, value);
            log.info("Hystrix - set {} = {} to redis", key, value);
        }
        return true;
    }

    @Override
    protected Boolean getFallback() {
        log.info("Hystrix - 降级 - set {} = {}, expire = {}", key, value, expire);
        return false;
    }
}
