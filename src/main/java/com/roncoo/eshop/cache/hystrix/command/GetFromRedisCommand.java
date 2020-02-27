package com.roncoo.eshop.cache.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import redis.clients.jedis.JedisCluster;

public class GetFromRedisCommand extends HystrixCommand<String> {

    private JedisCluster jedisCluster;

    private String key;

    public GetFromRedisCommand(JedisCluster jedisCluster, String key) {
        super(HystrixCommandGroupKey.Factory.asKey("RedisGroup"));
        this.jedisCluster = jedisCluster;
        this.key = key;
    }

    @Override
    protected String run() throws Exception {
        return jedisCluster.get(key);
    }

}
