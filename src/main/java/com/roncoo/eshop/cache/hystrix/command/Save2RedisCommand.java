package com.roncoo.eshop.cache.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import redis.clients.jedis.JedisCluster;

public class Save2RedisCommand extends HystrixCommand<Boolean> {

    private JedisCluster jedisCluster;

    private String key;
    private String value;
    private int expire;

    public Save2RedisCommand(JedisCluster jedisCluster, String key, String value, int expire) {
        super(HystrixCommandGroupKey.Factory.asKey("RedisGroup"));
        this.jedisCluster = jedisCluster;
        this.key = key;
        this.value = value;
        this.expire = expire;
    }

    @Override
    protected Boolean run() throws Exception {
        if (expire > 0) {
            jedisCluster.set(key, value);
            jedisCluster.expire(key, expire);
        } else {
            jedisCluster.set(key, value);
        }
        return true;
    }


}
