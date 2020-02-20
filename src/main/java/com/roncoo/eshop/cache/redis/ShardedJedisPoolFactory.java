package com.roncoo.eshop.cache.redis;

import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @description:
 * @author: lzp
 * @date: 2018/1/24
 */
@Component
public class ShardedJedisPoolFactory {

    // @Autowired
    public ShardedJedisPool shardedJedisPool;

    public ShardedJedis getShardedJedis() {
        return shardedJedisPool.getResource();
    }

}
